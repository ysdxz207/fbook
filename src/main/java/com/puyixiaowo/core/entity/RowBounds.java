package com.puyixiaowo.core.entity;


import java.io.Serializable;

public class RowBounds implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1432242532800200927L;

	private int offset = 0;
	private int limit = Integer.MAX_VALUE;
	
	public RowBounds() {
		super();
	}
	
	public RowBounds(int offset, int limit) {

		this.offset = offset;
		this.limit = limit;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}
}
