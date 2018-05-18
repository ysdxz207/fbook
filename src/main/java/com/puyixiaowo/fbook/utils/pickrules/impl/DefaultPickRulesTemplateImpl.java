package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.puyixiaowo.fbook.bean.book.BookBean;
import static com.puyixiaowo.fbook.utils.StringUtils.*;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import static com.puyixiaowo.fbook.utils.HtmlUtils.*;



/**
 *
 * @author Moses
 * @date 2018-05-17 17:38:11
 * 默认爬取规则模版实现
 *
 */

public class DefaultPickRulesTemplateImpl implements PickRulesTemplate{

    @Override
    public String getSearchLink(String keywords) {
        return null;
    }

    @Override
    public String getSearchEncoding() {
        return null;
    }

    @Override
    public Elements getSearchItems(Document document) {
        return null;
    }

    @Override
    public String getSearchItemAId(Element element) {
        return null;
    }

    @Override
    public String getSearchItemTitle(Element element) {
        return null;
    }

    @Override
    public String getSearchItemAuthor(Element element) {
        return null;
    }

    @Override
    public String getSearchItemCategory(Element element) {
        return null;
    }

    @Override
    public String getSearchItemUpdateDate(Element element) {
        return null;
    }

    @Override
    public String getSearchItemUpdateChapter(Element element) {
        return null;
    }

    @Override
    public String getSearchItemFaceUrl(Element element) {
        return null;
    }

    @Override
    public String getBookEncoding() {
        return null;
    }

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
    public String getBookDetailDescription(Document document) {
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
    public Elements getChapterListItems(Document document) {
        return null;
    }

    @Override
    public String getChapterListLink(BookBean bookBean) {
        return null;
    }

    @Override
    public String getChapterListTitle(Element element) {
        return null;
    }

    @Override
    public String getChapterListDetailLink(Element element) {
        return null;
    }

    @Override
    public String getChapterDetailLink(BookBean bookBean) {
        return null;
    }

    @Override
    public String getChapterDetailTitle(Document document) {
        return document.select(".readTitle").first().textNodes().get(0).text();
    }

    @Override
    public String getChapterDetailContent(Document document) {
        String content = "";

        String [] arrPagenum = document.select(".readTitle small").text().split("\\/");

        int currentpagenum = arrPagenum.length == 1 ? 1 : Integer.valueOf(arrPagenum[0].replaceAll("[^0-9]", ""));
        int maxpagenum = arrPagenum.length == 1 ? 1 : Integer.valueOf(arrPagenum[1].replaceAll("[^0-9]", ""));

        List<TextNode> textNodeList = document.select("#htmlContent").get(0).textNodes();


        int [] arrShouldRemove = {0, 1, 2};
        Iterator<TextNode> itContents = textNodeList.iterator();
        StringBuilder sbContent = new StringBuilder();

        int elementIndex = 0;
        while (itContents.hasNext()) {
            TextNode textNode = itContents.next();
            boolean breakWhile = false;
            for (int shouldRemoveIndex :
                    arrShouldRemove) {
                if (shouldRemoveIndex == elementIndex) {
                    textNode.remove();
                    breakWhile = true;
                    break;
                }
            }

            elementIndex ++;


            if (breakWhile) {
                continue;
            }
            String tempHtml = textNode.outerHtml();
            sbContent.append(tempHtml);
            if (isNotBlank(tempHtml)) {
                sbContent.append("</br>");
            }
        }

        content = sbContent.toString();

        if (currentpagenum < maxpagenum) {
            String url = document.baseUri().replace(".html", "_" + (currentpagenum + 1) + ".html");
            Connection.Response response = accessPage(url, Connection.Method.GET, "GBK");
            if (response != null) {
                try {
                    return content + getChapterDetailContent(response.parse());
                } catch (IOException e) {
                    return content;
                }
            }
        }
        return content;
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
