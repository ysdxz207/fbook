package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.fbook.annotation.Table;

import java.io.Serializable;

@Table("bookshelf")
public class BookshelfBean implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	private Long bookId;
	private Long createTime;
	private Integer pageMethod;



	public Long getId (){
		return id;
	}

	public void setId (Long id){
		this.id = id;
	}

	public Long getUserId (){
		return userId;
	}

	public void setUserId (Long userId){
		this.userId = userId;
	}

	public Long getBookId(){
		return bookId;
	}

	public void setBookId(Long bookId){
		this.bookId = bookId;
	}

	public Long getCreateTime (){
		return createTime;
	}

	public void setCreateTime (Long createTime){
		this.createTime = createTime;
	}

	public Integer getPageMethod() {
		return pageMethod;
	}

	public void setPageMethod(Integer pageMethod) {
		this.pageMethod = pageMethod;
	}
}