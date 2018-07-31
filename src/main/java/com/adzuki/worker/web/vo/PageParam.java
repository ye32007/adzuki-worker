package com.adzuki.worker.web.vo;

import java.io.Serializable;

public class PageParam implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PageParam(){}
	
	public PageParam(Integer pageNum, Integer pageSize) {
		super();
		this.pageNum = pageNum;
		this.pageSize = pageSize;
	}

	private Integer pageNum = 1; 		// 当前页数
	private Integer pageSize = 15;	 	// 每页记录数

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String toString() {
		return "PageParam [pageNum=" + pageNum + ", pageSize=" + pageSize + "]";
	}
}
