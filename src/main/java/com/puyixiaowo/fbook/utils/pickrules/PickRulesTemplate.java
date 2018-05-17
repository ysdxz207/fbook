package com.puyixiaowo.fbook.utils.pickrules;

import org.jsoup.nodes.Document;

public interface PickRulesTemplate {
    public String getBookDetailTitle(Document document);
    public String getBookDetailAuthor(Document document);
    public String getBookDetailUpdateDate(Document document);
    public String getBookDetailUpdateChapter(Document document);
    public String getBookDetailCategory(Document document);
    public String getBookDetailFaceUrl(Document document);
    public String getBookDetailIntro(Document document);

    public String getChapterListTitle(Document document);
    public String getChapterListLink(Document document);

    public String getChapterDetailTitle(Document document);
    public String getChapterDetailContent(Document document);
    public String getChapterDetailMaxPage(Document document);
    public String getChapterDetailCurrentPage(Document document);
}
