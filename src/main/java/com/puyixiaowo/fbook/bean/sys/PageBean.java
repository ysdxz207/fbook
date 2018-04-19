/**
 * 
 */
package com.puyixiaowo.fbook.bean.sys;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.core.entity.RowBounds;
import com.puyixiaowo.fbook.constants.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huangfeihong
 * @date 2016年12月8日 下午10:20:18
 */
public class PageBean<T> extends ResponseBean{
	private static final long serialVersionUID = 4728599360521173588L;
	//默认查询所有，由构造方法new RowBounds()确定
	private int pageSize = Constants.DEFAULT_PAGE_SIZE;
	private int pageCurrent = 1;
	private int totalCount = 0;
	private int pageTotal = 0;
	private String order = "id";
	private Boolean reverse = false;
	private RowBounds rowBounds;
	private List<T> list = new ArrayList<>();


	/**
	 * 
	 */
	public PageBean() {
		this.rowBounds = new RowBounds();//默认查所有
		this.setMessage("");//关闭成功提示框
		this.order = "id";
		this.reverse = true;
	}
	
	/**
	 * 
	 */
	public PageBean(int pageCurrent, int pageSize) {
		this.pageCurrent = pageCurrent;
		this.pageSize = pageSize;
		this.rowBounds = buidRowBounds();
		this.setMessage("");//关闭成功提示框
	}

	public PageBean(int pageCurrent, int pageSize,
					String order,
					Boolean reverse) {
		this(pageCurrent, pageSize);
		this.order = order;
		this.reverse = reverse;
	}
	
	/**
	 * 
	 */
	public PageBean(boolean selectAll) {
		if (selectAll) {
			this.rowBounds = new RowBounds();
		} else {
			this.rowBounds = buidRowBounds();
		}
		this.setMessage("");//关闭成功提示框
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public RowBounds getRowBounds() {
		return rowBounds;
	}

	public void setRowBounds(RowBounds rowBounds) {
		this.rowBounds = rowBounds;
	}

	public int getPageCurrent() {
		return pageCurrent;
	}

	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Boolean getReverse() {
		return reverse;
	}

	public void setReverse(Boolean reverse) {
		this.reverse = reverse;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
		//计算总页数
		int page = totalCount / pageSize;
		int yu = totalCount % pageSize;
		this.pageTotal = page == 0 ? 1 : (yu == 0 ? page : page + 1);
	}

	public int getPageTotal() {
		return pageTotal;
	}

	public void setPageTotal(int pageTotal) {
		this.pageTotal = pageTotal;
	}
	///////////////////////

	private RowBounds buidRowBounds() {
		int offset = (pageCurrent - 1) * pageSize ;
		RowBounds bouds = new RowBounds(offset, pageSize);
		if (!bouds.equals(rowBounds)) {
			return bouds;
		}
		
		return rowBounds;
	}

	@Override
	public String serialize() {

		return JSON.toJSONString(this);
	}
}
