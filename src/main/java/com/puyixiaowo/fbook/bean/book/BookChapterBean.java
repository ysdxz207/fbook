package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.Transient;

import java.io.Serializable;

public class BookChapterBean extends Validatable implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private Long bookId;
    private String title;
    private String content;
    private String link;


    @Transient
    private String source;
    @Transient
    private Boolean hasRead = false;
    @Transient
    private Integer chapterNum;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Integer getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(Integer chapterNum) {
        this.chapterNum = chapterNum;
    }
}