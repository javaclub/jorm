package com.github.javaclub.jorm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 与具体ORM实现无关的分页参数及查询结果封装.
 * 注意所有序号从1开始.
 * 
 * @param <T> Page中记录的类型.
 * 
 * @author calvin
 * @author gerald
 */
public class Page<T> implements Serializable {
	
	private static final long serialVersionUID = -8745138167696978267L;
	
	//-- 公共变量 --//
	

	//-- 分页参数 --//
	protected int pageNo = 1;                // 当前页页号，注意页号是从1开始的
	protected int pageSize = 15;              // 分页模型每页的大小
	protected boolean autoCount = true;      // 是否统计数据表中的总记录数

	/** 返回结果集 */
	protected List<T> result = Collections.emptyList();
	
	/** 总记录数 */
	protected long totalCount = -1;

	//-- 构造函数 --//
	public Page() {
	}

	public Page(final int pageSize) {
		setPageSize(pageSize);
	}

	public Page(final int pageSize, final boolean autoCount) {
		setPageSize(pageSize);
		setAutoCount(autoCount);
	}

	//-- 访问查询参数函数 --//
	/**
	 * 获得当前页的页号,序号从1开始,默认为1.
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
	 */
	public void setPageNo(final int pageNo) {
		this.pageNo = pageNo;

		if (pageNo < 1) {
			this.pageNo = 1;
		}
	}

	/**
	 * 获得每页的记录数量,默认为15.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * 设置每页的记录数量,低于1时自动调整为1.
	 */
	public void setPageSize(final int pageSize) {
		this.pageSize = pageSize;

		if (pageSize < 1) {
			this.pageSize = 1;
		}
	}

	/**
	 * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从0开始.
	 */
	public int getFirst() {
		return ((pageNo - 1) * pageSize);
	}
	
	/**
	 * 根据pageNo和pageSize计算当前页最后一条记录在总结果集中的位置,序号从0开始.
	 */
	public long getLast() {
		return Long.valueOf((getFirst() + pageSize - 1) + "");
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数, 默认为true.
	 */
	public boolean isAutoCount() {
		return autoCount;
	}

	/**
	 * 查询对象时是否自动另外执行count查询获取总记录数.
	 */
	public void setAutoCount(final boolean autoCount) {
		this.autoCount = autoCount;
	}

	//-- 访问查询结果函数 --//

	/**
	 * 取得页内的记录列表.
	 */
	public List<T> getResult() {
		return result;
	}

	/**
	 * 设置页内的记录列表.
	 */
	public void setResult(final List<T> result) {
		this.result = result;
	}
	
	/**
	 * 取得当前页的实际记录总数
	 *
	 * @return 当前页的实际记录总数.
	 */
	public int getPageResultCount() {
		return result.size();
	}

	/**
	 * 取得总记录数, 默认值为-1.
	 */
	public long getTotalCount() {
		return totalCount;
	}

	/**
	 * 设置总记录数.
	 */
	public void setTotalCount(final long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {
		if (totalCount < 0)
			return -1;

		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}
	
	/**
	 * 是否还有下一页.
	 */
	public boolean hasNext() {
		return (pageNo + 1 <= getTotalPages());
	}
	
	/**
	 * 是否还有上一页.
	 */
	public boolean hasPrev() {
		return (pageNo - 1 >= 1);
	}

	/**
	 * 取得下页的页号, 序号从1开始.
	 * 当前页为尾页时仍返回尾页序号.
	 */
	public int getNextPageNo() {
		if (hasNext())
			return pageNo + 1;
		else
			return pageNo;
	}

	/**
	 * 取得上页的页号, 序号从1开始.
	 * 当前页为首页时返回首页序号.
	 */
	public int getPrevPageNo() {
		if (hasPrev())
			return pageNo - 1;
		else
			return pageNo;
	}
	
	/**
	 * 取得下一个Page模型，如果没有就返回当前Page
	 *
	 * @return 结果集未初始化的Page模型
	 */
	public Page<T> getNextPage() {
		if(!hasNext()) {
			return this;
		}
		Page<T> page = new Page<T>(this.getPageSize(), true);
		page.setPageNo(this.getPageNo() + 1);
		page.setPageSize(this.getPageSize());
		page.setResult(new ArrayList<T>());
		// page.setTotalCount(this.getTotalCount()); // 会自动计算的，因为autoCount = true
		
		return page;
	}
	
	/**
	 * 取得上一个Page模型，如果没有就返回当前Page
	 *
	 * @return 结果集未初始化的Page模型
	 */
	public Page<T> getPrevPage() {
		if(!hasPrev()) {
			return this;
		}
		Page<T> page = new Page<T>(this.getPageSize(), true);
		page.setPageNo(this.getPageNo() - 1);
		page.setPageSize(this.getPageSize());
		page.setResult(new ArrayList<T>());
		// page.setTotalCount(this.getTotalCount()); // 会自动计算的，因为autoCount = true
		
		return page;
	}

	@Override
	public String toString() {
		return "Page [autoCount=" + autoCount + ", pageNo=" + pageNo
				+ ", pageSize=" + pageSize + ", resultSize=" + result.size()
				+ ", totalCount=" + totalCount + "]";
	}
	
	
}
