package com.puyixiaowo.fbook.utils.pickrules;

import com.puyixiaowo.fbook.bean.book.BookBean;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface PickRulesTemplate {

    //搜索
    public String getSearchLink(String keywords);
    public Elements getSearchItems(Document document);
    public String getSearchItemAId(Element element);
    public String getSearchItemTitle(Element element);
    public String getSearchItemAuthor(Element element);
    public String getSearchItemCategory(Element element);
    public String getSearchItemUpdateDate(Element element);
    public String getSearchItemUpdateChapter(Element element);
    public String getSearchItemFaceUrl(Element element);

    //详情
    public String getBookDetailLink(BookBean bookBean);
    public String getBookDetailTitle(Document document);
    public String getBookDetailAuthor(Document document);
    public String getBookDetailUpdateDate(Document document);
    public String getBookDetailUpdateChapter(Document document);
    public String getBookDetailCategory(Document document);
    public String getBookDetailFaceUrl(Document document);
    public String getBookDetailIntro(Document document);

    //章节列表
    public String getChapterListLink(BookBean bookBean);
    public String getChapterListTitle(Document document);
    public String getChapterListLink(Document document);

    //章节内容
    public String getChapterDetailLink(BookBean bookBean);
    public String getChapterDetailTitle(Document document);
    public String getChapterDetailContent(Document document);
    public String getChapterDetailMaxPage(Document document);
    public String getChapterDetailCurrentPage(Document document);
}