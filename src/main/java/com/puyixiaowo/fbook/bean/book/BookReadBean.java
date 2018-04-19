package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.NotNull;
import com.puyixiaowo.fbook.annotation.Table;

import java.io.Serializable;

@Table("book_read")
public class BookReadBean extends Validatable implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long userId;
	@NotNull
	private Long bookId;
	private String source;
	@NotNull
	private String lastReadingChapter;
	private Integer lastReadingChapterNum;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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
}