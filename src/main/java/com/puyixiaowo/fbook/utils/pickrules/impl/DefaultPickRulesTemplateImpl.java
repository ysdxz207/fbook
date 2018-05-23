package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.book.BookBean;
import static com.puyixiaowo.fbook.utils.StringUtils.*;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String getName() {
        return "乐文网";
    }

    @Override
    public String getSearchDevice() {
        return "PHONE";
    }

    @Override
    public String getSearchLink(String keywords) {
        try {
            return "http://www.lwxsw.cc/modules/article/search.php?searchkey=" + URLEncoder.encode(keywords, getSearchEncoding());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject getSearchParams(String keywords) {
        return null;
    }

    @Override
    public String getSearchMethod() {
        return "GET";
    }

    @Override
    public String getSearchEncoding() {
        return "GBK";
    }

    @Override
    public Elements getSearchItems(Document document) {
        document.select("tr").get(0).remove();
        return document.select("tr");
    }

    @Override
    public String getSearchItemBookIdThird(Element element) {
        Matcher matcherBookIdThird = Pattern.compile("http\\:\\/\\/.*\\/.*\\/(.*)\\/").matcher(element.select("td").get(0).select("a").attr("href"));
        String bookIdThird = matcherBookIdThird.find() ? matcherBookIdThird.group(1) : "";
        return bookIdThird;
    }

    @Override
    public String getSearchItemTitle(Element element) {
        return element.select("td").get(0).select("a").text();
    }

    @Override
    public String getSearchItemAuthor(Element element) {
        return element.select("td").get(2).text();
    }

    @Override
    public String getSearchItemCategory(Element element) {
        return "未知";
    }

    @Override
    public String getSearchItemUpdateDate(Element element) {
        return element.select("td").get(4).text();
    }

    @Override
    public String getSearchItemUpdateChapter(Element element) {
        return element.select("td").get(1).text();
    }

    @Override
    public String getSearchItemFaceUrl(Element element) {
        return null;
    }

    @Override
    public String getBookEncoding() {
        return "GBK";
    }

    @Override
    public String getBookDetailLink(BookBean bookBean) {
        return "http://www.lwxsw.cc/book/" + bookBean.getBookIdThird() + "/";
    }

    @Override
    public String getBookDetailTitle(Document document) {
        return document.select(".bookTitle").text();
    }

    @Override
    public String getBookDetailAuthor(Document document) {
        return document.select(".booktag").get(0).getAllElements().eachText().get(1);
    }

    @Override
    public String getBookDetailUpdateDate(Document document) {
        return document.select("p.visible-xs").text().split("：")[1];
    }

    @Override
    public String getBookDetailUpdateChapter(Document document) {
        return document.select("p").get(1).select("a").text().replace("正文 ", "");
    }

    @Override
    public String getBookDetailCategory(Document document) {
        return document.select(".booktag").get(0).getAllElements().eachText().get(2);
    }

    @Override
    public String getBookDetailDescription(Document document) {
        return document.select("#bookIntro").text();
    }

    @Override
    public String getBookDetailFaceUrl(Document document) {
        return document.select("#bookIntro img").attr("src");
    }

    @Override
    public Elements getChapterListItems(Document document) {
        return document.select("#list-chapterAll .panel-chapterlist dd a");
    }

    @Override
    public String getChapterListLink(BookBean bookBean) {
        return "http://www.lwxsw.cc/book/" + bookBean.getBookIdThird() + "/";
    }

    @Override
    public String getChapterListTitle(Element element) {
        return element.text();
    }

    @Override
    public String getChapterListDetailLink(Element element) {
        return element.baseUri() + element.attr("href");
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
            Connection.Response response = getPage(url, "GBK");
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
}
