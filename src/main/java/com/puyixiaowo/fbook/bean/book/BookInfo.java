package com.puyixiaowo.fbook.bean.book;

import java.io.Serializable;

/**
 * 仅用于前端展示，不写入数据库
 */
public class BookInfo implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long bookId;
    private String description;
    private String rating;
    private String retentionRatio;
    private String updated;
    private String category;


    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRetentionRatio() {
        return retentionRatio;
    }

    public void setRetentionRatio(String retentionRatio) {
        this.retentionRatio = retentionRatio;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
