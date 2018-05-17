package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.nodes.Document;

/**
 *
 * @author Moses
 * @date 2018-05-17 17:38:11
 * 默认爬取规则模版实现
 *
 */

public class DefaultPickRulesTemplateImpl implements PickRulesTemplate{

    @Override
    public String getBookDetailLink(BookBean bookBean) {
        return null;
    }

    @Override
    public String getBookDetailTitle(Document document) {
        return null;
    }

    @Override
    public String getBookDetailAuthor(Document document) {
        return null;
    }

    @Override
    public String getBookDetailUpdateDate(Document document) {
        return null;
    }

    @Override
    public String getBookDetailUpdateChapter(Document document) {
        return null;
    }

    @Override
    public String getBookDetailCategory(Document document) {
        return null;
    }

    @Override
    public String getBookDetailFaceUrl(Document document) {
        return null;
    }

    @Override
    public String getBookDetailIntro(Document document) {
        return null;
    }

    @Override
    public String getChapterListLink(BookBean bookBean) {
        return null;
    }

    @Override
    public String getChapterListTitle(Document document) {
        return null;
    }

    @Override
    public String getChapterListLink(Document document) {
        return null;
    }

    @Override
    public String getChapterDetailLink(BookBean bookBean) {
        return null;
    }

    @Override
    public String getChapterDetailTitle(Document document) {
        return null;
    }

    @Override
    public String getChapterDetailContent(Document document) {
        return null;
    }

    @Override
    public String getChapterDetailMaxPage(Document document) {
        return null;
    }

    @Override
    public String getChapterDetailCurrentPage(Document document) {
        return null;
    }
}
