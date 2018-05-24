package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.Connection;
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

import static com.puyixiaowo.fbook.utils.HtmlUtils.getPage;
import static com.puyixiaowo.fbook.utils.StringUtils.isNotBlank;


/**
 * @author Moses
 * @date 2018-05-17 17:38:11
 * 3z中文网爬取规则模版实现
 */

public class ZzzPickRulesTemplateImpl extends DefaultPickRulesTemplateImpl implements PickRulesTemplate {

    @Override
    public String getName() {
        return "五湖中文网";
    }

    @Override
    public String getSearchDevice() {
        return "PC";
    }

    @Override
    public String getSearchLink(String keywords) {
        return "http://www.530p.com/s/" + keywords;
    }

    @Override
    public JSONObject getSearchParams(String keywords) {
        return null;
    }

    @Override
    public String getSearchMethod() {
        return "POST";
    }

    @Override
    public String getSearchEncoding() {
        return "GBK";
    }

    @Override
    public Elements getSearchItems(Document document) {
        Elements elsChapterList = document.select("#list");
        Elements elsUls = document.select("tr#nr");

        if (elsChapterList.size() == 0
                && elsUls.size() > 1) {
            return elsUls;
        }

        Elements es = new Elements();
        if (elsChapterList.size() > 0) {
            Element element = new Element("tr");
            element.append("<td><a href=\"" + document.baseUri() + "\">" + getBookDetailTitle(document) + "</a></td>");
            element.append("<td><a>" + getBookDetailUpdateChapter(document) + "</a></td>");
            element.append("<td>" + getBookDetailAuthor(document) + "</td>");
            element.append("<td></td>");
            element.append("<td>" + getBookDetailUpdateDate(document) + "</td>");
            element.append("<td></td>");
            es.add(element);
            return es;
        }
        return es;
    }

    @Override
    public String getSearchItemBookIdThird(Element element) {
        Matcher matcherBookIdThird = Pattern.compile(".*\\/.*\\/(.*)\\/").matcher(element.select("a").attr("href"));
        String bookIdThird = matcherBookIdThird.find() ? matcherBookIdThird.group(1) : "";
        return bookIdThird;
    }

    @Override
    public String getSearchItemTitle(Element element) {
        return element.select("td a").get(0).text();
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
        return "http://www.5hzw.com/" + bookBean.getBookIdThird() + "/";
    }

    @Override
    public String getBookDetailTitle(Document document) {
        return document.select("#info h1").text();
    }

    @Override
    public String getBookDetailAuthor(Document document) {
        return document.select("#info p").get(0).text().replace("作 者：", "");
    }

    @Override
    public String getBookDetailUpdateDate(Document document) {
        return document.select("#info p").get(2).text().replace("最后更新：", "");
    }

    @Override
    public String getBookDetailUpdateChapter(Document document) {
        return document.select("dd").get(0).text();
    }

    @Override
    public String getBookDetailCategory(Document document) {
        return document.select(".con_top a").last().text();
    }

    @Override
    public String getBookDetailDescription(Document document) {
        return document.select("#intro").text();
    }

    @Override
    public String getBookDetailFaceUrl(Document document) {
        return "http://www.5hzw.com" + document.select("#fmimg img").attr("src");
    }

    @Override
    public Elements getChapterListItems(Document document) {
        return document.select("#chapterlist li");
    }

    @Override
    public String getChapterListLink(BookBean bookBean) {
        return "http://www.5hzw.com/" + bookBean.getBookIdThird() + "/";
    }

    @Override
    public String getChapterListTitle(Element element) {
        return element.text();
    }

    @Override
    public String getChapterListDetailLink(Element element) {
        return "https://m.w23us.com" + element.select("a").attr("href");
    }

    @Override
    public String getChapterDetailTitle(Document document) {
        return document.select(".readTitle").first().textNodes().get(0).text();
    }

    @Override
    public String getChapterDetailContent(Document document) {
        String content = "";

        String[] arrPagenum = document.select(".readTitle small").text().split("\\/");

        int currentpagenum = arrPagenum.length == 1 ? 1 : Integer.valueOf(arrPagenum[0].replaceAll("[^0-9]", ""));
        int maxpagenum = arrPagenum.length == 1 ? 1 : Integer.valueOf(arrPagenum[1].replaceAll("[^0-9]", ""));

        List<TextNode> textNodeList = document.select("#htmlContent").get(0).textNodes();


        int[] arrShouldRemove = {0, 1, 2};
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

            elementIndex++;


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
