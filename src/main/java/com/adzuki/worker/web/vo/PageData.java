package com.adzuki.worker.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageData<T> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long total;			//总条数
	private List<T> rows;		//结果对象
	
	public PageData() {}

	public PageData(long total, List<T> rows) {
		this.total = total;
		this.rows = rows;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static PageData<?> createEmpty() {
		return new PageData(0, new ArrayList());
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	@Override
	public String toString() {
		return "PageData [total=" + total + ", rows=" + rows + "]";
	}
}
