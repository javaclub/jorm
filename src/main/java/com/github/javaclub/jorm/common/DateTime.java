/*
 * @(#)DateTime.java  2009-2-19
 *
 * Copyright (c) 2009 by gerald. All Rights Reserved.
 */

package com.github.javaclub.jorm.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A utilities class for processing datetime
 *
 * @author <a href="mailto:gerald.chen.hz@gmail.com">Gerald Chen</a>
 * @version $Id: DateTime.java 56 2011-06-27 02:11:24Z gerald.chen.hz@gmail.com $
 */
public final class DateTime {

	/** The delimiter in date format string */
	public static final String DATE_SEPARATOR = "-";

	/** The delimiter in date time string */
	public static final String TIME_SEPARATOR = ":";

	/** The delimiter between date and time */
	public static final String DATE_TIME_SEPARATOR = " ";

	/** The date format string:(yyyy-MM-dd) */
	public static final String DATE_FORMAT_STR = "yyyy-MM-dd";

	/** The time format string:(HH:mm:ss) */
	public static final String TIME_FORMAT_STR = "HH:mm:ss";

	/** The timestamp format string:(yyyy-MM-dd HH:mm:ss) */
	public static final String TIMESTAMP_STR = "yyyy-MM-dd HH:mm:ss";

	/** A MSEL format string:(yyyy-MM-dd HH:mm:ss,SSS) */
	public static final String MSEL_FORMAT_STR_1 = "yyyy-MM-dd HH:mm:ss,SSS";

	/** A MSEL format string:(yyyy-MM-dd HH:mm:ss.SSS) */
	public static final String MSEL_FORMAT_STR_2 = "yyyy-MM-dd HH:mm:ss.SSS";

	/** The length of date format string (yyyy-MM-dd) 10 */
	public static final int DATE_FORMAT_STR_LENGTH = 10;

	/** The length of date timestamp format string (yyyy-MM-dd HH:mm:ss) 19 */
	public static final int TIMESTAMP_STR_LENGTH = 19;

	/** The length of date MSEL format string(yyyy-MM-dd HH:mm:ss,SSS) 23 */
	public static final int MSEL_FORMAT_STR_LENGTH = 23;

	/** the millisecond in one minute */
	public static final long MILLIS_IN_ONE_MIN = 60000;// 1000 * 60

	/** the millisecond in one hour */
	public static final long MILLIS_IN_ONE_HOUR = 3600000;// MILLIS_IN_ONE_MIN * 60

	/** the millisecond in one day */
	public static final long MILLIS_IN_ONE_DAY = 86400000;// MILLIS_IN_ONE_HOUR * 24
	
	/** 日期/时间的各个部分标识：年（1） */
    public final static int YEAR = 1;

    /** 日期/时间的各个部分标识：月（2） */
    public final static int MONTH = 2;

    /** 日期/时间的各个部分标识：日（3） */
    public final static int DAY = 3;

    /** 日期/时间的各个部分标识：时（4） */
    public final static int HOUR = 4;

    /** 日期/时间的各个部分标识：分（5） */
    public final static int MINUTE = 5;

    /** 日期/时间的各个部分标识：秒（6） */
    public final static int SECOND = 6;
    
    /** 日期/时间的各个部分标识：毫秒（7） */
    public final static int MILLISECOND = 7;

    /** 日期/时间的各个部分标识：一刻钟（11） */
    public final static int QUATER = 11;

    /** 日期/时间的各个部分标识：一周（12） */
    public final static int WEEK = 12;

    /** 当月天数（13） */
    public final static int DAYS_OF_MONTH = 13;

    /** 当月周数（14） */
    public final static int WEEKS_OF_MONTH = 14;

    /** 当年天数（15） */
    public final static int DAYS_OF_YEAR = 15;

    /** 当年周数（16） */
    public final static int WEEKS_OF_YEAR = 16;

	/** The days in months of a year, which is not a leap year */
	public static final int[] DAYS = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	
	protected static final Log LOG = LogFactory.getLog(DateTime.class);

	/**
	 * Converts a java.sql.Date to java.util.Date
	 * <p>
	 * In fact, this method is unnecessary because java.sql.Date extends from java.util.Date
	 * 
	 * @param date a java.sql.Date
	 * @return a java.util.Date
	 */
	public static java.util.Date toDate(java.sql.Date date) {
		// return new java.util.Date(date.getTime());
		return date;
	}
	
	/**
	 * Converts a java.sql.Timestamp to java.util.Date
	 * <p>
	 * In fact, this method is unnecessary because java.sql.Timestamp extends from java.util.Date
	 * 
	 * @param timestamp a java.sql.Timestamp
	 * @return a java.util.Date
	 */
	public static java.util.Date toDate(java.sql.Timestamp timestamp) {
		// return new java.util.Date(timestamp.getTime());
		return timestamp;
	}
	
	/**
	 * parse string of format to java.util.Date.
	 * <p>
	 * 'yyyy-MM-dd'、'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss,SSS' is supported.
	 * 
	 * @param obj the input.
	 * @return a java.util.Date
	 */
	public static java.util.Date toDate(Object obj) {
		if(obj == null) {
			return null;
		}
		if (obj instanceof java.util.Date || obj instanceof java.sql.Date
				|| obj instanceof java.sql.Timestamp) {
			return (java.util.Date) obj;
		}
		if (obj instanceof String) {
			return toDate(obj.toString()); 
		}
		throw new RuntimeException("the parameter [" + obj + " can't be converted to java.util.Date");
	}

	/**
	 * parse string of format to java.util.Date.
	 * <p>
	 * 'yyyy-MM-dd'、'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss,SSS' is supported.
	 * 
	 * @param input the input string.
	 * @return a java.util.Date
	 */
	public static java.util.Date toDate(String input) {
		String dealStr = input.trim();
		// if the first character is '''
		int pos = dealStr.indexOf("'");
		if (pos == 0) {
			dealStr = dealStr.substring(1, dealStr.length());
		}
		// if the last character is '''
		pos = dealStr.lastIndexOf("'");
		if (pos == dealStr.length() - 1) {
			dealStr = dealStr.substring(0, dealStr.length() - 1);
		}

		int length = dealStr.length();
		SimpleDateFormat sdf = null;
		if (length == DATE_FORMAT_STR_LENGTH && dealStr.charAt(4) == '-' && dealStr.charAt(7) == '-') {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		} else if (length == TIMESTAMP_STR_LENGTH && dealStr.charAt(4) == '-' && dealStr.charAt(7) == '-' 
			&& dealStr.charAt(10) == ' ' && dealStr.charAt(13) == ':' && dealStr.charAt(16) == ':') {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (length == MSEL_FORMAT_STR_LENGTH && dealStr.charAt(4) == '-' && dealStr.charAt(7) == '-' 
			&& dealStr.charAt(10) == ' ' && dealStr.charAt(13) == ':' && dealStr.charAt(16) == ':' 
			&& (dealStr.charAt(19) == ',' || dealStr.charAt(19) == '.')) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			if (dealStr.charAt(19) == '.') {
				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			}
		}
		if (null == sdf) {
			throw new IllegalArgumentException("the format of [" + input + "] is not supported...");
		}
		try {
			return sdf.parse(dealStr);
		} catch (ParseException e) {
			if(LOG.isWarnEnabled()) {
				LOG.warn("error occured when parsing String [" + input + "] to java.util.Date with the format [" 
						+ sdf.toPattern() + "].");
			}
		}
		return null;
	}

	/**
	 * parse a java.util.Date by the given date string and format pattern
	 * 
	 * @param date   the given date string
	 * @param pattern the given format pattern
	 * @return a java.util.Date
	 */
	public static java.util.Date toDate(String date, String pattern) {
		try {
			return new SimpleDateFormat(pattern).parse(date);
		} catch (ParseException e) {
		}
		return null;
	}
	
	/**
	 * Converts a java.util.Date to java.sql.Date
	 * 
	 * @param date a java.util.Date
	 * @return a java.sql.Date
	 */
	public static java.sql.Date toSqlDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
	
	/**
	 * Converts a java.lang.Object to java.sql.Date
	 * 
	 * @param obj a java.lang.Object
	 * @return a java.sql.Date
	 */
	public static java.sql.Date toSqlDate(Object obj) {
		if(obj == null) {
			return null;
		}
		if (obj instanceof java.sql.Date) {
			return (java.sql.Date) obj; 
		}
		Date date = toDate(obj);
		return new java.sql.Date(date.getTime());
	}

	/**
	 * Converts a java.util.Date to java.sql.Timestamp
	 * 
	 * @param date a java.util.Date
	 * @return a java.sql.Timestamp
	 */
	public static java.sql.Timestamp toTimestamp(java.util.Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
	
	/**
	 * Converts a java.lang.Object to java.sql.Timestamp
	 * 
	 * @param obj a java.lang.Object
	 * @return a java.sql.Timestamp
	 */
	public static java.sql.Timestamp toTimestamp(Object obj) {
		if(obj == null) {
			return null;
		}
		if (obj instanceof java.sql.Timestamp) {
			return (java.sql.Timestamp) obj; 
		}
		Date date = toDate(obj);
		return new java.sql.Timestamp(date.getTime());
	}
	
	public static java.sql.Time toTime(Object obj) {
		if(obj == null) {
			return null;
		}
		if (obj instanceof java.sql.Time) {
			return (java.sql.Time) obj; 
		} else if (obj instanceof java.sql.Timestamp) {
			return new java.sql.Time(((java.sql.Timestamp) obj).getTime());
		} else if (obj instanceof java.sql.Date) {
			return new java.sql.Time(((java.sql.Timestamp) obj).getTime());
		} else if(obj instanceof java.util.Date) {
			return new java.sql.Time(((java.util.Date) obj).getTime());
		} else if(obj instanceof String) {
			return java.sql.Time.valueOf(obj.toString());
		} else {
			throw new IllegalArgumentException("the parameter {@obj[" + obj + "]} is a bad Argument, which can't be converted to java.sql.Time.");
		}
	}


	/**
	 * Returns the current datetime.
	 * 
	 * @return the current datetime.
	 */
	public static Date now() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	 * Returns the current identity like '20090831182858668'
	 *
	 * @return
	 */
	public static String nowIdentity() {
		return getFormat(now(), "yyyyMMddHHmmssSSS");
	}

	/**
	 * parse string of format to java.util.Date.
	 * <p>
	 * 'yyyy-MM-dd'、'yyyy-MM-dd HH:mm:ss' or 'yyyy-MM-dd HH:mm:ss,SSS' is supported.
	 * 
	 * @param inputStr the input string.
	 * @return a java.util.Date
	 * @deprecated use {@link parseDate(java.lang.String)}
	 */
	public static java.util.Date parse(String inputStr) {
		String dealStr = inputStr.trim();
		// if the first character is '''
		int pos = dealStr.indexOf("'");
		if (pos == 0) {
			dealStr = dealStr.substring(1, dealStr.length());
		}
		// if the last character is '''
		pos = dealStr.lastIndexOf("'");
		if (pos == dealStr.length() - 1) {
			dealStr = dealStr.substring(0, dealStr.length() - 1);
		}

		int length = dealStr.length();
		StringTokenizer tokens = null;
		if (length == DATE_FORMAT_STR_LENGTH) {
			tokens = new StringTokenizer(dealStr, DATE_SEPARATOR, false);

			// 取得年月日字符值
			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day);
			java.util.Date date = c.getTime();
			return date;
		} else if (length == TIMESTAMP_STR_LENGTH) {
			String dateStr = dealStr.substring(0, dealStr.indexOf(DATE_TIME_SEPARATOR));
			String timeStr = dealStr.substring(dealStr.indexOf(DATE_TIME_SEPARATOR) + 1);

			tokens = new StringTokenizer(dateStr, DATE_SEPARATOR, false);
			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			tokens = new StringTokenizer(timeStr, TIME_SEPARATOR, false);
			int hour = new Integer(tokens.nextToken()).intValue();
			int minute = new Integer(tokens.nextToken()).intValue();
			int second = new Integer(tokens.nextToken()).intValue();

			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day, hour, minute, second);
			java.util.Date date = c.getTime();
			return date;
		} else if (length == MSEL_FORMAT_STR_LENGTH) {
			String dateStr = dealStr.substring(0, dealStr.indexOf(DATE_TIME_SEPARATOR));
			String timeStr = dealStr.substring(dealStr.indexOf(DATE_TIME_SEPARATOR) + 1, 19);
			int i = dealStr.lastIndexOf(",");
			if (i < 0) {
				i = dealStr.lastIndexOf(".");
			}
			String mselStr = dealStr.substring(i + 1);

			tokens = new StringTokenizer(dateStr, DATE_SEPARATOR, false);
			int year = new Integer(tokens.nextToken()).intValue();
			int month = new Integer(tokens.nextToken()).intValue();
			int day = new Integer(tokens.nextToken()).intValue();

			tokens = new StringTokenizer(timeStr, TIME_SEPARATOR, false);
			int hour = new Integer(tokens.nextToken()).intValue();
			int minute = new Integer(tokens.nextToken()).intValue();
			int second = new Integer(tokens.nextToken()).intValue();
			int msel = new Integer(mselStr).intValue();
			Calendar c = Calendar.getInstance();
			c.set(year, month - 1, day, hour, minute, second);
			c.set(Calendar.MILLISECOND, msel);
			java.util.Date date = c.getTime();
			return date;
		} else {
			return null;
		}
	}
	
	/**
	 * getting a date string like 'yyyy-MM-dd' from a java.util.Date or java.sql.Date
	 * 
	 * @param date a java.util.Date or java.sql.Date
	 * @return a string like 'yyyy-MM-dd'
	 */
	public static String getDateFormat(java.util.Date date) {
		return getFormat(date, DATE_FORMAT_STR);
	}

	/**
	 * 从java.util.Date得到时间串"HH:mm:ss"
	 * 
	 * @param date 日期参数，类型可以是java.util.Date或java.sql.Date
	 * @return
	 */
	public static String getTimeFormat(java.util.Date date) {
		return getFormat(date, TIME_FORMAT_STR);
	}

	/**
	 * 从java.util.Date得到时间戳"yyyy-MM-dd HH:mm:ss"
	 * 
	 * @param date 日期参数，类型可以是java.util.Date或java.sql.Date
	 * @return
	 */
	public static String getTimestampFormat(java.util.Date date) {
		return getFormat(date, TIMESTAMP_STR);
	}

	/**
	 * gets current time's string format like "yyyy-MM-dd"
	 * 
	 * @return
	 */
	public static String currentDate() {
		return getDateFormat(now());
	}

	/**
	 * gets current time's string format like "HH:mm:ss"
	 * 
	 * @return
	 */
	public static String currentTime() {
		return getTimeFormat(now());
	}

	/**
	 * gets current time's string format like "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String currentTimestamp() {
		return getTimestampFormat(now());
	}
	
	/**
	 * gets current time's string format like "yyyy-MM-dd HH:mm:ss,SSS"
	 * 
	 * @return
	 */
	public static String currentMselFormat() {
		return getFormat(now(), "yyyy-MM-dd HH:mm:ss,SSS");
	}

	/**
	 * 取得java.util.Date类型日期的格式化串，格式化格式为parseFormat
	 * 
	 * @param date 日期参数，类型可以是java.util.Date或java.sql.Date
	 * @param parseFormat 日期格式化串
	 * @return 当date为<code>null</code>时，返回<code>null</code>;当parseFormat为<code>null</code>时，默认返回date.toString()
	 */
	public static String getFormat(java.util.Date date, String parseFormat) {
		if (null == date) {
			return null;
		}
		if (null == parseFormat || "".equalsIgnoreCase(parseFormat)) {
			return date.toString();
		}
		return new SimpleDateFormat(parseFormat).format(date);
	}

	/**
	 * 计算两个任意时间中间的间隔天数
	 * 
	 * @param startday 起始时间
	 * @param endday 结束时间
	 * @return
	 */
	public static long getIntervalDays(Calendar startday, Calendar endday) {
		if (startday.after(endday)) {
			Calendar cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTimeInMillis();
		long el = endday.getTimeInMillis();

		long cl = el - sl;
		return (cl / (1000 * 60 * 60 * 24));
	}

	/**
	 * 计算两个任意时间中间的间隔天数
	 * 
	 * @param startday 起始时间
	 * @param endday 结束时间
	 * @return
	 */
	public static long getIntervalDays(java.util.Date startday, java.util.Date endday) {
		if (startday.after(endday)) {
			java.util.Date cal = startday;
			startday = endday;
			endday = cal;
		}
		long sl = startday.getTime();
		long el = endday.getTime();
		long cl = el - sl;
		return (cl / (1000 * 60 * 60 * 24));
	}

	/**
	 * 判断指定年份是否为闰年
	 * 
	 * @param year 需要被判断的年份
	 * @return <code>true</code> or <code>false</code>
	 */
	public static boolean isLeapYear(int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}

	/**
	 * 从某个具体日期java.util.Date倒退millis毫秒
	 * 
	 * @param date
	 * @param millis
	 * @return
	 */
	public static java.util.Date backward(java.util.Date date, long millis) {
		java.util.Date d = new java.util.Date();
		d.setTime(date.getTime() - millis);
		return d;
	}

	/**
	 * 从某个具体日期java.util.Date向前推进millis毫秒
	 * 
	 * @param date
	 * @param millis
	 * @return
	 */
	public static java.util.Date forward(java.util.Date date, long millis) {
		java.util.Date d = new java.util.Date();
		d.setTime(date.getTime() + millis);
		return d;
	}
	
	/**
     * Gets the field of a date.
     * 
     * @param date a date given
     * @param field the date field
     * @return the field value
     * @throws RuntimeException if the field is invalid.
     */
    public static int get(Date date, int field) throws RuntimeException {
        if (date == null) {
            throw new RuntimeException("date is null");
        }

        GregorianCalendar cal = toCalendar(date);
        switch (field) {
            case YEAR: {
                return cal.get(Calendar.YEAR);
            }
            case MONTH: {
                return cal.get(Calendar.MONTH) + 1;
            }
            case DAY: {
                return cal.get(Calendar.DAY_OF_MONTH);
            }
            case HOUR: {
                return cal.get(Calendar.HOUR_OF_DAY);
            }
            case MINUTE: {
                return cal.get(Calendar.MINUTE);
            }
            case SECOND: {
                return cal.get(Calendar.SECOND);
            }
            case MILLISECOND: {
                return cal.get(Calendar.MILLISECOND);
            }
            case WEEK: {
                //REM: cal.setFirstDayOfWeek( Calendar.MONDAY )
                //     is not used, because it doesn't work!
                return (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7;
            }
            case DAYS_OF_MONTH: {
                return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            case WEEKS_OF_MONTH: {
                return cal.getActualMaximum(Calendar.WEEK_OF_MONTH);
            }
            case DAYS_OF_YEAR: {
                return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
            }
            case WEEKS_OF_YEAR: {
                return cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
            }
            default: {
                throw new RuntimeException("invalid date field " + field);
            }
        }//end case
    }
    
    /**
     * Adds the specified (signed) amount of time to the given time field, based
     * on the calendar's rules.
     * 
     * @param date the datetime value to be added.
     * @param field the time field.
     * @param amount the amount of date or time to be added to the field.
     */
    public static Date add(Date date, int field, int amount) {
        Calendar cal = toCalendar(date);
        int nCalendarField;
        switch (field) {
            case YEAR:
                nCalendarField = Calendar.YEAR;
                break;
            case MONTH:
                nCalendarField = Calendar.MONTH;
                break;
            case WEEK:
                nCalendarField = Calendar.WEEK_OF_YEAR;
                break;
            case DAY:
                nCalendarField = Calendar.DAY_OF_YEAR;
                break;
            case HOUR:
                nCalendarField = Calendar.HOUR;
                break;
            case MINUTE:
                nCalendarField = Calendar.MINUTE;
                break;
            case SECOND:
                nCalendarField = Calendar.SECOND;
                break;
            case MILLISECOND:
                nCalendarField = Calendar.MILLISECOND;
                break;
            default:
                throw new RuntimeException("invalid date time field: " + field);
        }
        cal.add(nCalendarField, amount);
        return cal.getTime();
    }
	
	/**
     * 增加n天后的日期时间
     * @param date
     * @param day
     * @return
     */
    public Date add(Date date,int day){
        Date newDate = null;
        // 使用默认时区和语言环境获得一个日历
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);// 设置日历时间
        // 设置增加的时间:当day为负数时-为设置时间的前几天，正数则为增加的天数
        cal.add(Calendar.DAY_OF_MONTH, day);
        newDate = cal.getTime();
        return newDate;
    }

	/**
	 * Get Calendar instance by specified year, month and day.
	 * 
	 * @param year 4-digit year.
	 * @param month Month range is 1-12.
	 * @param day Day range is 1-?, end depends on year and month.
	 * @return A Calendar instance.
	 */
	public static Calendar getCalendar(int year, int month, int day) {
		if (year < 2000 || year > 2100)
			throw new IllegalArgumentException();
		if (month < 1 || month > 12)
			throw new IllegalArgumentException();
		if (day < 1)
			throw new IllegalArgumentException();
		if (month == 2 && isLeapYear(year)) {
			if (day > 29)
				throw new IllegalArgumentException();
		} else {
			if (day > DAYS[month - 1])
				throw new IllegalArgumentException();
		}
		month--;
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	/**
	 * Return today's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT).
	 */
	public static int[] getToday() {
		return getDate(Calendar.getInstance());
	}

	/**
	 * Return day's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT).
	 */
	public static int[] getDate(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		return getDate(c);
	}

	/**
	 * Return day's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT).
	 */
	public static int[] getDate(Calendar c) {
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0)
			week = 7;
		return new int[] { c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), week };
	}

	/**
	 * Return day's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT), hour, minute, second.
	 */
	public static int[] getTime(long t) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(t);
		return getTime(c);
	}
	
	/**
	 * Return day's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT), hour, minute, second.
	 *
	 * @param date
	 * @return
	 */
	public static int[] getTime(Date date) {
		return getTime(date.getTime());
	}

	/**
	 * Return day's year, month(1-12), day(1-31), week(0-6, for SUN, MON, ... SAT), hour, minute, second.
	 */
	public static int[] getTime(Calendar c) {
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0)
			week = 7;
		return new int[] { c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), week,
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND) };
	}

	/**
	 * 得到昨天的年月日值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static int[] getPreviousDay(int year, int month, int day) {
		day--;
		if (day < 1) {
			month--;
			if (month < 1) {
				year--;
				month = 12;
			}
			int lastDay = DAYS[month - 1];
			if (month == 2 && isLeapYear(year))
				lastDay++;
			day = lastDay;
		}
		return new int[] { year, month, day };
	}

	/**
	 * 得到明天的年月日值
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static int[] getNextDay(int year, int month, int day) {
		day++;
		int max = DAYS[month - 1];
		if (month == 2 && isLeapYear(year))
			max++;
		if (day > max) {
			day = 1;
			month++;
			if (month > 12) {
				year++;
				month = 1;
			}
		}
		return new int[] { year, month, day };
	}
	
	/**
     * Converts date to calendar.
     * 
     * @param date a date given
     * @return the related calendar
     */
    public static GregorianCalendar toCalendar(Date date) {
        GregorianCalendar cal = new java.util.GregorianCalendar();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTime(date);
        return cal;
    }

	/**
	 * Generates a random java.util.Date between two long data.
	 * 
	 * @param begin a long type data.
	 * @param end a long type data, must be greater than begin. 
	 * @return a random java.util.Date
	 */
	public static java.util.Date randomDate(long begin, long end) {
		if (begin >= end) {
			throw new IllegalArgumentException("end must greater than begin...");
		}
		long rtn = begin + (long) (Math.random() * (end - begin));
		if (rtn == begin || rtn == end) {
			return randomDate(begin, end);
		}
		return new Date(rtn);
	}

	/**
	 * Generates a random java.util.Date between two date format strings.
	 *
	 * @param beginDate a date format string.
	 * @param endDate a date format string.
	 * @return a random java.util.Date
	 */
	public static java.util.Date randomDate(String beginDate, String endDate) {
		Date begin = toDate(beginDate);
		Date end = toDate(endDate);
		if (null == begin || null == end || begin.getTime() >= end.getTime()) {
			return null;
		}
		return randomDate(begin.getTime(), end.getTime());
	}
	
	/**
     * 检查一个时间是否接近于另一个时间。
     * 
     * @param date 要比较的时间
     * @param baseDate 基础时间
     * @param seconds 秒数
     * 
     * @return 如果 date 在 baseDate前后 seonds 秒数，则返回true，否则返回false。
     */
	public static boolean isClosely(Date date, Date baseDate, int seconds) {
		long m_time = date.getTime();
		long b_time = baseDate.getTime();
		long ms = seconds * 1000L;

		if (m_time == b_time)
			return true;

		if (m_time > b_time) { // 现在时间在基础时间之前
			return b_time + ms > m_time;
		} else if (m_time < b_time) { // 现在时间在基础时间之后
			return m_time + ms > b_time;
		} else { // 同一个时间
			return true;
		}
	}
	
	/**
     * 将使用的毫秒数转化为可读的字符串, 如1天1小时1分1秒. <BR>
     * <code>assertEquals("1天1小时1分1秒", DateUtil.timeToString(90061000));</code>
     * @param msUsed 使用的毫秒数.
     * @return 可读的字符串, 如1天1小时1分1秒.
     */
	public static String timeSpan(long msUsed) {
		if (msUsed < 0) {
			return String.valueOf(msUsed);
		}
		if (msUsed < 1000) {
			return String.valueOf(msUsed) + "毫秒";
		}
		// 长于1秒的过程，毫秒不计
		msUsed /= 1000;
		if (msUsed < 60) {
			return String.valueOf(msUsed) + "秒";
		}
		if (msUsed < 3600) {
			long nMinute = msUsed / 60;
			long nSecond = msUsed % 60;
			return String.valueOf(nMinute) + "分" + String.valueOf(nSecond)
					+ "秒";
		}
		// 3600 * 24 = 86400
		if (msUsed < 86400) {
			long nHour = msUsed / 3600;
			long nMinute = (msUsed - nHour * 3600) / 60;
			long nSecond = (msUsed - nHour * 3600) % 60;
			return String.valueOf(nHour) + "小时" + String.valueOf(nMinute) + "分"
					+ String.valueOf(nSecond) + "秒";
		}

		long nDay = msUsed / 86400;
		long nHour = (msUsed - nDay * 86400) / 3600;
		long nMinute = (msUsed - nDay * 86400 - nHour * 3600) / 60;
		long nSecond = (msUsed - nDay * 86400 - nHour * 3600) % 60;
		return String.valueOf(nDay) + "天" + String.valueOf(nHour) + "小时" 
				+ String.valueOf(nMinute) + "分" + String.valueOf(nSecond) + "秒";
	}

	public static void main(String[] args) {
		String input = "2008-05-18 22:18:58";
		Date date = toDate(input);
		System.out.println(date);
		
		System.out.println(get(new Date(), DateTime.DAY));
		
		System.out.println(nowIdentity());
		
		System.out.println(toTime(new Date()));
		
		System.out.println();
	}
}
