package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.NotNull;
import com.puyixiaowo.fbook.annotation.Table;
import com.puyixiaowo.fbook.annotation.Transient;

import java.io.Serializable;

@Table("book")
public class BookBean extends Validatable implements Serializable{
	private static final long serialVersionUID = 1L;

	private Long id;
	private String author;
	private String faceUrl;
	@NotNull
	private String bookIdThird;//api接口书ID
	private String name;
	private String url;
	private Long createTime;
	private Integer isOver;
	private Boolean useApi;


	//
	@Transient
	private BookInfo bookInfo;
	@Transient
	private Boolean onShelf;

	public Long getId (){
		return id;
	}

	public void setId (Long id){
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFaceUrl() {
		return faceUrl;
	}

	public void setFaceUrl(String faceUrl) {
		this.faceUrl = faceUrl;
	}

	public String getBookIdThird() {
		return bookIdThird;
	}

	public void setBookIdThird(String bookIdThird) {
		this.bookIdThird = bookIdThird;
	}

	public String getName (){
		return name;
	}

	public void setName (String name){
		this.name = name;
	}

	public String getUrl (){
		return url;
	}

	public void setUrl (String url){
		this.url = url;
	}

	public Long getCreateTime (){
		return createTime;
	}

	public void setCreateTime (Long createTime){
		this.createTime = createTime;
	}

	public Integer getIsOver() {
		return isOver;
	}

	public void setIsOver(Integer isOver) {
		this.isOver = isOver;
	}

	public BookInfo getBookInfo() {
		return bookInfo;
	}

	public void setBookInfo(BookInfo bookInfo) {
		this.bookInfo = bookInfo;
	}

	public Boolean getOnShelf() {
		return onShelf;
	}

	public void setOnShelf(Boolean onShelf) {
		this.onShelf = onShelf;
	}

	public Boolean getUseApi() {
		return useApi;
	}

	public void setUseApi(Boolean useApi) {
		this.useApi = useApi;
	}
}