package com.github.javaclub.jorm.jdbc;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaclub.jorm.Jorm;
import com.github.javaclub.jorm.Session;
import com.github.javaclub.jorm.annotation.Basic;
import com.github.javaclub.jorm.annotation.Column;
import com.github.javaclub.jorm.annotation.Id;
import com.github.javaclub.jorm.annotation.NoColumn;
import com.github.javaclub.jorm.common.CaseInsensitiveMap;
import com.github.javaclub.jorm.common.CommonUtil;
import com.github.javaclub.jorm.common.Reflections;
import com.github.javaclub.jorm.common.Strings;
import com.github.javaclub.jorm.config.JdbcConfigXmlParser;
import com.github.javaclub.jorm.id.IdentifierGeneratorFactory;
import com.github.javaclub.jorm.jdbc.process.FieldProcessor;
import com.github.javaclub.jorm.jdbc.sql.SqlParams;
import com.github.javaclub.jorm.jdbc.work.Isolater;
import com.github.javaclub.jorm.jdbc.work.internal.AssociatedLoadingWork;

/**
 * <p>
 * <code>BeanPersister</code> matches column names to bean property names 
 * and converts <code>ResultSet</code> columns into objects for those bean 
 * properties.  Subclasses should override the methods in the processing chain
 * to customize behavior.
 * </p>
 * 
 * <p>
 * This class is thread-safe.
 * </p>
 *
 * 
 */
@SuppressWarnings("unchecked")
public class BeanPersister {
    
    /**
     * Special array value used by <code>mapColumnsToProperties</code> that 
     * indicates there is no bean property that matches a column from a 
     * <code>ResultSet</code>.
     */
    protected static final int PROPERTY_NOT_FOUND = -1;
    
    protected Session session;
    
    /**
     * Set a bean's primitive properties to these defaults when SQL NULL 
     * is returned.  These are the same as the defaults that ResultSet get* 
     * methods return in the event of a NULL column.
     */
	private static final Map<Class,Object> primitiveDefaults = new HashMap<Class,Object>();

    static {
        primitiveDefaults.put(Integer.TYPE, 0);
        primitiveDefaults.put(Short.TYPE, (short) 0);
        primitiveDefaults.put(Byte.TYPE, (byte) 0);
        primitiveDefaults.put(Float.TYPE, (float) 0);
        primitiveDefaults.put(Double.TYPE, (double) 0);
        primitiveDefaults.put(Long.TYPE, (long) 0);
        primitiveDefaults.put(Boolean.TYPE, Boolean.FALSE);
        primitiveDefaults.put(Character.TYPE, '\u0000');
    }

    /**
     * Constructor for BeanProcessor.
     */
    public BeanPersister() {
        super();
    }
    
    public BeanPersister(Session session) {
		super();
		this.session = session;
	}

    public Serializable getIdentifierValue(Object obj) throws JdbcException {
    	Field identifierField = ClassMetadata.getClassMetadata(obj.getClass()).identifierField;
    	if(null == identifierField) return null;
    	Serializable generateId;
    	try {
			Id id = identifierField.getAnnotation(Id.class);
			generateId = IdentifierGeneratorFactory.create(id.value(), session.getDialect()).generate(session, obj, identifierField);
			// 同时将Id值赋给目标实体
			Reflections.setFieldValue(obj, identifierField, CommonUtil.convert(identifierField, generateId));
			return generateId;
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			throw new JdbcException(npe);
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdbcException(e);
		} finally {
			identifierField = null;
			generateId = null;
		}
    }
    
    public <T> SqlParams<T> insert(T model) throws JdbcException {
		ClassMetadata metadata = ClassMetadata.getClassMetadata(model.getClass());
		boolean initializedInsert = (metadata.insert == null ? false : true);
		Object[] params = new Object[metadata.insertFields.size()];
		String tbname = metadata.tableName;

		StringBuilder sql = new StringBuilder();
		if(!initializedInsert) {
			sql.append("INSERT INTO ").append(tbname + "(");
		}
		
		// 需要往数据库中插入值的属性
		Field field = null;
		for (int i = 0; i < metadata.insertFields.size(); i++) {
			field = metadata.insertFields.get(i);
			
			if (!initializedInsert) {
				if (i > 0) {
					sql.append(", ");
				}
				sql.append(metadata.column(field.getName()));
			}
			
			if(ClassMetadata.hasProcessor(metadata.insertFields.get(i))) {
				Class<FieldProcessor> clazz = field.getAnnotation(Basic.class).processor();
				FieldProcessor fp = Reflections.newInstance(clazz);
				try {
					params[i] = fp.insert(session, model, field);
				} catch (SQLException e) {
					e.printStackTrace();
					throw new JdbcException(e);
				}
			} else {
				params[i] = Reflections.getFieldValue(model, field);
			}
		}
		if (!initializedInsert) {
			sql.append(") VALUES(");
		}

		if (!initializedInsert) {
			for (int i = 0; i < params.length; i++) {
				if (i > 0) {
					sql.append(", ");
				}
				sql.append("?");
			}
			sql.append(")");
			ClassMetadata.getClassMetadata(model.getClass()).insert = sql.toString();
		}
		SqlParams<T> sqlParams = new SqlParams<T>(ClassMetadata.getClassMetadata(model.getClass()).insert, params);
		try {
			return sqlParams;
		} finally {
			sql.setLength(0);
			sql = null;
			params = null;
			tbname = null;
			metadata = null;
			sqlParams = null;
		}
	}

	/**
     * Convert a <code>ResultSet</code> row into a JavaBean.  This 
     * implementation uses reflection and <code>BeanInfo</code> classes to 
     * match column names to bean property names.  Properties are matched to 
     * columns based on several factors:
     * <br/>
     * <ol>
     *     <li>
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     </li>
     * 
     *     <li>
     *     The column type can be converted to the property's set method 
     *     parameter type with a ResultSet.get* method.  If the conversion fails
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
     *     </li>
     * </ol>
     * 
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to 
     * <code>null</code> when SQL NULL is returned.  This is the same behavior
     * as the <code>ResultSet</code> get* methods.
     * </p>
     *
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @throws SQLException if a database access error occurs
     * @return the newly created bean
     */
    public <T> T toBean(ResultSet rs, Class<T> type) throws SQLException {

        // PropertyDescriptor[] props = propertyDescriptors(type);
    	Field[] fields = Reflections.getFields(type);

        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToField = mapColumnsToFields(rsmd, fields);

        return createBean(rs, type, fields, columnToField);
    }
    
    public <T> T toBean(ResultSet rs, Class<T> type, boolean loadAssociated) throws SQLException {

        // PropertyDescriptor[] props = propertyDescriptors(type);
    	Field[] fields = Reflections.getFields(type);

        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToField = mapColumnsToFields(rsmd, fields);

        return createBean(rs, type, fields, columnToField, loadAssociated);
    }
    
    /**
     * Convert a <code>ResultSet</code> into a <code>List</code> of JavaBeans.  
     * This implementation uses reflection and <code>BeanInfo</code> classes to 
     * match column names to bean property names. Properties are matched to 
     * columns based on several factors:
     * <br/>
     * <ol>
     *     <li>
     *     The class has a writable property with the same name as a column.
     *     The name comparison is case insensitive.
     *     </li>
     * 
     *     <li>
     *     The column type can be converted to the property's set method 
     *     parameter type with a ResultSet.get* method.  If the conversion fails
     *     (ie. the property was an int and the column was a Timestamp) an
     *     SQLException is thrown.
     *     </li>
     * </ol>
     * 
     * <p>
     * Primitive bean properties are set to their defaults when SQL NULL is
     * returned from the <code>ResultSet</code>.  Numeric fields are set to 0
     * and booleans are set to false.  Object bean properties are set to 
     * <code>null</code> when SQL NULL is returned.  This is the same behavior
     * as the <code>ResultSet</code> get* methods.
     * </p>
     *
     * @param rs ResultSet that supplies the bean data
     * @param type Class from which to create the bean instance
     * @param loadAssociated Whether to load the associated object
     * @throws SQLException if a database access error occurs
     * @return the newly created List of beans
     */
    public <T> List<T> toBeanList(ResultSet rs, Class<T> type, boolean loadAssociated) throws SQLException {
        List<T> results = new ArrayList<T>();
        if (!rs.next()) {
            return results;
        }

        Field[] fields = Reflections.getFields(type);
        ResultSetMetaData rsmd = rs.getMetaData();
        int[] columnToProperty = mapColumnsToFields(rsmd, fields);

        do {
            results.add(createBean(rs, type, fields, columnToProperty, loadAssociated));
        } while (rs.next());

        return results;
    }
    
    /**
     * Create a <code>Map</code> from the column values in one
     * <code>ResultSet</code> row.  The <code>ResultSet</code> should be
     * positioned on a valid row before
     * passing it to this method.  Implementations of this method must not
     * alter the row position of the <code>ResultSet</code>.
     *
     * @param rs ResultSet that supplies the map data
     * @throws SQLException if a database access error occurs
     * @return the newly created Map
     */
    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        Map<String,Object> result = new CaseInsensitiveMap<String,Object>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int cols = rsmd.getColumnCount();

        for (int i = 1; i <= cols; i++) {
            //metaData.getColumnName(i).trim().length() < 1 ? metaData.getColumnLabel(i) : metaData.getColumnName(i)
            result.put(rsmd.getColumnLabel(i), rs.getObject(i));
        }

        return result;
    }

	public Object getColumnValue(ResultSet rs, String columnLabel)
			throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			String colName = rsmd.getColumnName(i);
			String colLabel = rsmd.getColumnLabel(i);
			if ((Strings.isNotEmpty(colName) && columnLabel
					.equalsIgnoreCase(colName))
					|| (Strings.isNotEmpty(colLabel) && columnLabel
							.equalsIgnoreCase(colLabel))) {
				return rs.getObject(i);
			}
		}
		return null;
	}
	
	public Object loadBean(Class persistentClass, Serializable id) throws SQLException {
    	ClassMetadata metadata = ClassMetadata.getClassMetadata(persistentClass);
    	String load = "SELECT * FROM " + metadata.tableName 
    				+ " WHERE " 
    				+ metadata.column(metadata.identifierField.getName()) + " = ?";
    	if(JdbcConfigXmlParser.isShowSql()) {
			Jorm.format(load);
		}
    	Connection connection = this.session.getConnection();
		try {
			PreparedStatement pstmt = connection.prepareStatement(load);
			try {
				JdbcUtil.setParameters(pstmt, new Object[] { id });
				ResultSet rs = pstmt.executeQuery();
				try {
					if (rs.next()) {
						Field[] fields = Reflections.getFields(persistentClass);
						ResultSetMetaData rsmd = rs.getMetaData();
						int[] columnToField = mapColumnsToFields(rsmd, fields);

						return initializeBean(persistentClass, rs, fields, columnToField);
					}
				} finally {
					rs.close();
				}
			} finally {
				pstmt.close();
			}
		} catch (SQLException sqle) {
			throw sqle;
		}
		return null;
    }
	
    /**
     * Creates a new object and initializes its fields from the ResultSet.
     *
     * @param rs The result set.
     * @param type The bean type (the return type of the object).
     * @param fields The property descriptors.
     * @param columnToField The column indices in the result set.
     * @return An initialized object.
     * @throws SQLException if a database error occurs.
     */
	protected <T> T createBean(ResultSet rs, Class<T> type,
    		Field[] fields, int[] columnToField)
            throws SQLException {
    	if (CommonUtil.isNativeType(type)) {
    		int idx = 1;
			if (rs.getMetaData().getColumnCount() != 1) {
				/*throw new SQLException("ResultSet returned [" + rs.getMetaData().getColumnCount()
						+ "] columns but 1 column was expected to load data into an instance of ["
						+ type.getName() + "]");*/
				String columnName = "";
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					idx = (i + 1);
					columnName = rs.getMetaData().getColumnLabel(idx);
					if ((!(columnName.startsWith(Jorm.IGNORE_COLUMN_PREFIX) && columnName.endsWith(Jorm.IGNORE_COLUMN_SUFFIX)))
							&& CommonUtil.matched(type, rs.getMetaData().getColumnClassName(idx))) {
						break;
					}
				}
			}
			return (T) JdbcUtil.getValueFromResultSet(rs, idx, type);
		}
    	
		return (T) initializeBean(type, rs, fields, columnToField);
    }
	
	protected <T> T createBean(ResultSet rs, Class<T> type,
    		Field[] fields, int[] columnToField, boolean loadAssociated)
            throws SQLException {
    	if (CommonUtil.isNativeType(type)) {
    		int idx = 1;
			if (rs.getMetaData().getColumnCount() != 1) {
				/*throw new SQLException("ResultSet returned [" + rs.getMetaData().getColumnCount()
						+ "] columns but 1 column was expected to load data into an instance of ["
						+ type.getName() + "]");*/
				String columnName = "";
				for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
					idx = (i + 1);
					columnName = rs.getMetaData().getColumnLabel(idx);
					if ((!(columnName.startsWith(Jorm.IGNORE_COLUMN_PREFIX) && columnName.endsWith(Jorm.IGNORE_COLUMN_SUFFIX)))
							&& CommonUtil.matched(type, rs.getMetaData().getColumnClassName(idx))) {
						break;
					}
				}
			}
			return (T) JdbcUtil.getValueFromResultSet(rs, idx, type);
		}
    	
		return (T) initializeBean(type, rs, fields, columnToField, loadAssociated);
    }
    
    protected Object initializeBean(Class persistentClass, ResultSet rs,
			Field[] fields, int[] columnToField) throws SQLException {
		Object bean = newInstance(persistentClass);
		for (int i = 1; i < columnToField.length; i++) {
			if (columnToField[i] == PROPERTY_NOT_FOUND) {
				continue;
			}
			Field field = fields[columnToField[i]];
			Class fieldType = field.getType();
			Object value = processColumn(rs, i, fieldType);
			if (ClassMetadata.hasProcessor(field)) {
				Class<FieldProcessor> clazz = field.getAnnotation(Basic.class).processor();
				FieldProcessor fp = newInstance(clazz);
				fp.load(this.session, bean, field, rs, i);
			} else {
				if (fieldType != null && value == null && fieldType.isPrimitive()) {
					value = primitiveDefaults.get(fieldType);
				}
				Reflections.setFieldValue(bean, field, CommonUtil.convert(field, value));
			}
		}
		// 加载关联对象
		AssociatedLoadingWork work = new AssociatedLoadingWork(bean);
		Isolater.doNonTransactedStepWork(work, session);
		
		return bean;
	}
    
    protected Object initializeBean(Class persistentClass, ResultSet rs,
			Field[] fields, int[] columnToField, boolean loadAssociated) throws SQLException {
		Object bean = newInstance(persistentClass);
		for (int i = 1; i < columnToField.length; i++) {
			if (columnToField[i] == PROPERTY_NOT_FOUND) {
				continue;
			}
			Field field = fields[columnToField[i]];
			Class fieldType = field.getType();
			Object value = processColumn(rs, i, fieldType);
			if (ClassMetadata.hasProcessor(field)) {
				Class<FieldProcessor> clazz = field.getAnnotation(Basic.class).processor();
				FieldProcessor fp = newInstance(clazz);
				fp.load(this.session, bean, field, rs, i);
			} else {
				if (fieldType != null && value == null && fieldType.isPrimitive()) {
					value = primitiveDefaults.get(fieldType);
				}
				Reflections.setFieldValue(bean, field, CommonUtil.convert(field, value));
			}
		}
		if(loadAssociated) { // 加载关联对象
			AssociatedLoadingWork work = new AssociatedLoadingWork(bean);
			Isolater.doNonTransactedStepWork(work, session);
		}
		return bean;
	}
    
	/**
     * Create an <code>Object[]</code> from the column values in one
     * <code>ResultSet</code> row.  The <code>ResultSet</code> should be
     * positioned on a valid row before passing it to this method.
     * Implementations of this method must not alter the row position of
     * the <code>ResultSet</code>.
     *
     * @param rs ResultSet that supplies the array data
     * @throws SQLException if a database access error occurs
     * @return the newly created array
     */
    public static Object[] toArray(final ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }
	
	/**
     * Factory method that returns a new instance of the given Class.  This
     * is called at the start of the bean creation process and may be 
     * overridden to provide custom behavior like returning a cached bean
     * instance.
     *
     * @param c The Class to create an object from.
     * @return A newly created object of the Class.
     * @throws SQLException if creation failed.
     */
    protected static <T> T newInstance(Class<T> c) throws SQLException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new SQLException(
                "Cannot create " + c.getName() + ": " + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException(
                "Cannot create " + c.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * The positions in the returned array represent column numbers.  The 
     * values stored at each position represent the index in the 
     * <code>PropertyDescriptor[]</code> for the bean property that matches 
     * the column name.  If no bean property was found for a column, the 
     * position is set to <code>PROPERTY_NOT_FOUND</code>.
     * 
     * @param rsmd The <code>ResultSetMetaData</code> containing column 
     * information.
     * 
     * @param fields The bean property descriptors.
     * 
     * @throws SQLException if a database access error occurs
     *
     * @return An int[] with column index to property index mappings.  The 0th 
     * element is meaningless because JDBC column indexing starts at 1.
     */
    protected static int[] mapColumnsToFields(ResultSetMetaData rsmd,
    		Field[] fields) throws SQLException {

        int cols = rsmd.getColumnCount();
        int columnToProperty[] = new int[cols + 1];
        Arrays.fill(columnToProperty, PROPERTY_NOT_FOUND);

        for (int col = 1; col <= cols; col++) {
            String columnName = rsmd.getColumnLabel(col);
            for (int i = 0; i < fields.length; i++) {
            	NoColumn noColumn = fields[i].getAnnotation(NoColumn.class);
    			if(null != noColumn) {
    				continue;
    			}
            	String realColName = fields[i].getName();
            	Column column = fields[i].getAnnotation(Column.class);
            	if(null != column && !Strings.isEmpty(column.value())) {
            		realColName = column.value();
            	}
                if (columnName.equalsIgnoreCase(realColName)) {
                    columnToProperty[col] = i;
                    break;
                }
            }
        }

        return columnToProperty;
    }

    /**
     * Convert a <code>ResultSet</code> column into an object.  Simple 
     * implementations could just call <code>rs.getObject(index)</code> while
     * more complex implementations could perform type manipulation to match 
     * the column's type to the bean property type.
     * 
     * <p>
     * This implementation calls the appropriate <code>ResultSet</code> getter 
     * method for the given property type to perform the type conversion.  If 
     * the property type doesn't match one of the supported 
     * <code>ResultSet</code> types, <code>getObject</code> is called.
     * </p>
     * 
     * @param rs The <code>ResultSet</code> currently being processed.  It is
     * positioned on a valid row before being passed into this method.
     * 
     * @param index The current column index being processed.
     * 
     * @param fieldType The bean property type that this column needs to be
     * converted into.
     * 
     * @throws SQLException if a database access error occurs
     * 
     * @return The object from the <code>ResultSet</code> at the given column
     * index after optional type processing or <code>null</code> if the column
     * value was SQL NULL.
     */
	protected static Object processColumn(ResultSet rs, int index, Class fieldType)
        throws SQLException {

        if (fieldType.equals(String.class)) {
            return rs.getString(index);
            
        } else if (
            fieldType.equals(Integer.TYPE) || fieldType.equals(Integer.class)) {
            return rs.getInt(index);

        } else if (
            fieldType.equals(Boolean.TYPE) || fieldType.equals(Boolean.class)) {
            return rs.getBoolean(index);

        } else if (fieldType.equals(Long.TYPE) || fieldType.equals(Long.class)) {
            return rs.getLong(index);

        } else if (
            fieldType.equals(Double.TYPE) || fieldType.equals(Double.class)) {
            return rs.getDouble(index);

        } else if (
            fieldType.equals(Float.TYPE) || fieldType.equals(Float.class)) {
            return rs.getFloat(index);

        } else if (
            fieldType.equals(Short.TYPE) || fieldType.equals(Short.class)) {
            return rs.getShort(index);

        } else if (fieldType.equals(Byte.TYPE) || fieldType.equals(Byte.class)) {
            return rs.getByte(index);
            
        } else if (fieldType.equals(Timestamp.class)) {
            return rs.getTimestamp(index);

        } else {
            return rs.getObject(index);
        }

    }

}
