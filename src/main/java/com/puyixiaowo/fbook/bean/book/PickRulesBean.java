package com.puyixiaowo.fbook.bean.book;

import java.io.Serializable;

public class PickRulesBean implements Serializable {
    private static final long serialVersionUID = -6808379757080016944L;

    private String id;

    private String bookDetailTitle;
    private String bookDetailAuthor;
    private String bookDetailUpdateDate;
    private String bookDetailUpdateChapter;
    private String bookDetailCategory;
    private String bookDetailFaceUrl;
    private String bookDetailIntro;

    private String chapterListTitle;
    private String chapterListLink;

    private String chapterDetailTitle;
    private String chapterDetailContent;
    private String chapterDetailMaxPage;
    private String chapterDetailCurrentPage;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookDetailTitle() {
        return bookDetailTitle;
    }

    public void setBookDetailTitle(String bookDetailTitle) {
        this.bookDetailTitle = bookDetailTitle;
    }

    public String getBookDetailAuthor() {
        return bookDetailAuthor;
    }

    public void setBookDetailAuthor(String bookDetailAuthor) {
        this.bookDetailAuthor = bookDetailAuthor;
    }

    public String getBookDetailUpdateDate() {
        return bookDetailUpdateDate;
    }

    public void setBookDetailUpdateDate(String bookDetailUpdateDate) {
        this.bookDetailUpdateDate = bookDetailUpdateDate;
    }

    public String getBookDetailUpdateChapter() {
        return bookDetailUpdateChapter;
    }

    public void setBookDetailUpdateChapter(String bookDetailUpdateChapter) {
        this.bookDetailUpdateChapter = bookDetailUpdateChapter;
    }

    public String getBookDetailCategory() {
        return bookDetailCategory;
    }

    public void setBookDetailCategory(String bookDetailCategory) {
        this.bookDetailCategory = bookDetailCategory;
    }

    public String getBookDetailFaceUrl() {
        return bookDetailFaceUrl;
    }

    public void setBookDetailFaceUrl(String bookDetailFaceUrl) {
        this.bookDetailFaceUrl = bookDetailFaceUrl;
    }

    public String getBookDetailIntro() {
        return bookDetailIntro;
    }

    public void setBookDetailIntro(String bookDetailIntro) {
        this.bookDetailIntro = bookDetailIntro;
    }

    public String getChapterListTitle() {
        return chapterListTitle;
    }

    public void setChapterListTitle(String chapterListTitle) {
        this.chapterListTitle = chapterListTitle;
    }

    public String getChapterListLink() {
        return chapterListLink;
    }

    public void setChapterListLink(String chapterListLink) {
        this.chapterListLink = chapterListLink;
    }

    public String getChapterDetailTitle() {
        return chapterDetailTitle;
    }

    public void setChapterDetailTitle(String chapterDetailTitle) {
        this.chapterDetailTitle = chapterDetailTitle;
    }

    public String getChapterDetailContent() {
        return chapterDetailContent;
    }

    public void setChapterDetailContent(String chapterDetailContent) {
        this.chapterDetailContent = chapterDetailContent;
    }

    public String getChapterDetailMaxPage() {
        return chapterDetailMaxPage;
    }

    public void setChapterDetailMaxPage(String chapterDetailMaxPage) {
        this.chapterDetailMaxPage = chapterDetailMaxPage;
    }

    public String getChapterDetailCurrentPage() {
        return chapterDetailCurrentPage;
    }

    public void setChapterDetailCurrentPage(String chapterDetailCurrentPage) {
        this.chapterDetailCurrentPage = chapterDetailCurrentPage;
    }
}
