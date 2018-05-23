package com.puyixiaowo.fbook.bean.book;

import com.puyixiaowo.core.entity.Validatable;
import com.puyixiaowo.fbook.annotation.Table;

import java.io.Serializable;

@Table("pick_rules")
public class PickRulesBean extends Validatable implements Serializable {
    private static final long serialVersionUID = -6808379757080016944L;

    private String id;

    private String searchDevice;
    private String searchEncoding;
    private String searchLink;
    private String searchMethod;
    private String searchParams;
    private String searchItems;
    private String searchItemBookIdThird;
    private String searchItemTitle;
    private String searchItemAuthor;
    private String searchItemCategory;
    private String searchItemUpdateDate;
    private String searchItemUpdateChapter;
    private String searchItemFaceUrl;

    private String bookDetailLink;
    private String bookDetailTitle;
    private String bookDetailAuthor;
    private String bookDetailUpdateDate;
    private String bookDetailUpdateChapter;
    private String bookDetailCategory;
    private String bookDetailDescription;
    private String bookDetailFaceUrl;

    private String chapterListItems;
    private String chapterListTitle;
    private String chapterListLink;
    private String chapterListDetailLink;

    private String chapterDetailTitle;
    private String chapterDetailContent;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchDevice() {
        return searchDevice;
    }

    public void setSearchDevice(String searchDevice) {
        this.searchDevice = searchDevice;
    }

    public String getSearchEncoding() {
        return searchEncoding;
    }

    public void setSearchEncoding(String searchEncoding) {
        this.searchEncoding = searchEncoding;
    }

    public String getSearchLink() {
        return searchLink;
    }

    public void setSearchLink(String searchLink) {
        this.searchLink = searchLink;
    }

    public String getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(String searchParams) {
        this.searchParams = searchParams;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public String getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(String searchItems) {
        this.searchItems = searchItems;
    }

    public String getSearchItemBookIdThird() {
        return searchItemBookIdThird;
    }

    public void setSearchItemBookIdThird(String searchItemBookIdThird) {
        this.searchItemBookIdThird = searchItemBookIdThird;
    }

    public String getSearchItemTitle() {
        return searchItemTitle;
    }

    public void setSearchItemTitle(String searchItemTitle) {
        this.searchItemTitle = searchItemTitle;
    }

    public String getSearchItemAuthor() {
        return searchItemAuthor;
    }

    public void setSearchItemAuthor(String searchItemAuthor) {
        this.searchItemAuthor = searchItemAuthor;
    }

    public String getSearchItemCategory() {
        return searchItemCategory;
    }

    public void setSearchItemCategory(String searchItemCategory) {
        this.searchItemCategory = searchItemCategory;
    }

    public String getSearchItemUpdateDate() {
        return searchItemUpdateDate;
    }

    public void setSearchItemUpdateDate(String searchItemUpdateDate) {
        this.searchItemUpdateDate = searchItemUpdateDate;
    }

    public String getSearchItemUpdateChapter() {
        return searchItemUpdateChapter;
    }

    public void setSearchItemUpdateChapter(String searchItemUpdateChapter) {
        this.searchItemUpdateChapter = searchItemUpdateChapter;
    }

    public String getSearchItemFaceUrl() {
        return searchItemFaceUrl;
    }

    public void setSearchItemFaceUrl(String searchItemFaceUrl) {
        this.searchItemFaceUrl = searchItemFaceUrl;
    }

    public String getBookDetailLink() {
        return bookDetailLink;
    }

    public void setBookDetailLink(String bookDetailLink) {
        this.bookDetailLink = bookDetailLink;
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

    public String getBookDetailDescription() {
        return bookDetailDescription;
    }

    public void setBookDetailDescription(String bookDetailDescription) {
        this.bookDetailDescription = bookDetailDescription;
    }

    public String getBookDetailFaceUrl() {
        return bookDetailFaceUrl;
    }

    public void setBookDetailFaceUrl(String bookDetailFaceUrl) {
        this.bookDetailFaceUrl = bookDetailFaceUrl;
    }

    public String getChapterListItems() {
        return chapterListItems;
    }

    public void setChapterListItems(String chapterListItems) {
        this.chapterListItems = chapterListItems;
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

    public String getChapterListDetailLink() {
        return chapterListDetailLink;
    }

    public void setChapterListDetailLink(String chapterListDetailLink) {
        this.chapterListDetailLink = chapterListDetailLink;
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

}
