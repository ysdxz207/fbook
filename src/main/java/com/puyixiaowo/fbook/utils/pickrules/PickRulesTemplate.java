package com.puyixiaowo.fbook.utils.pickrules;

import com.puyixiaowo.fbook.bean.book.BookBean;
import org.jsoup.nodes.Document;

public interface PickRulesTemplate {

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
