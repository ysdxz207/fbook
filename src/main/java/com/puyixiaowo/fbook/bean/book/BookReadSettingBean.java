package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.Table;
import com.puyixiaowo.fbook.annotation.Transient;

import java.io.Serializable;

@Table("book_read_setting")
public class BookReadSettingBean extends Validatable implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	private Long createTime;
	private String pageMethod;
	private String bgColor;
	private Integer fontSize;
	private Integer lineHeight;
	private String color;
	private Integer sort;
	private Boolean useApi;

	@Transient
	private String lastReadingChapter;
	@Transient
	private Integer lastReadingChapterNum;
	@Transient
	private Long bookId;



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

	public Long getCreateTime (){
		return createTime;
	}

	public void setCreateTime (Long createTime){
		this.createTime = createTime;
	}

	public String getPageMethod (){
		return pageMethod;
	}

	public void setPageMethod (String pageMethod){
		this.pageMethod = pageMethod;
	}

	public String getBgColor (){
		return bgColor;
	}

	public void setBgColor (String bgColor){
		this.bgColor = bgColor;
	}

	public Integer getFontSize (){
		return fontSize;
	}

	public void setFontSize (Integer fontSize){
		this.fontSize = fontSize;
	}

	public Integer getLineHeight (){
		return lineHeight;
	}

	public void setLineHeight (Integer lineHeight){
		this.lineHeight = lineHeight;
	}

	public Integer getSort (){
		return sort;
	}

	public void setSort (Integer sort){
		this.sort = sort;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean getUseApi() {
		return useApi;
	}

	public void setUseApi(Boolean useApi) {
		this.useApi = useApi;
	}

	public String getLastReadingChapter() {
		return lastReadingChapter;
	}

	public void setLastReadingChapter(String lastReadingChapter) {
		this.lastReadingChapter = lastReadingChapter;
	}

	public Integer getLastReadingChapterNum() {
		return lastReadingChapterNum;
	}

	public void setLastReadingChapterNum(Integer lastReadingChapterNum) {
		this.lastReadingChapterNum = lastReadingChapterNum;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
}