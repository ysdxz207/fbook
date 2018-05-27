package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.puyixiaowo.fbook.utils.HtmlUtils.getPage;


/**
 * @author Moses
 * @date 2018-05-17 17:38:11
 * 5hzw爬取规则模版实现
 */

public class DefaultPickRulesTemplateImpl extends LwxswPickRulesTemplateImpl implements PickRulesTemplate {

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
        try {
            return "http://www.5hzw.com/modules/article/search.php?searchkey=" + URLEncoder.encode(keywords, "GBK");
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
        return document.select("#list dd a");
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
        return "http://www.5hzw.com" + element.select("a").attr("href");
    }

    @Override
    public String getChapterDetailTitle(Document document) {
        return document.select(".bookname h1").text();
    }

    @Override
    public String getChapterDetailContent(Document document) {

        return document.select("#content").html();
    }
}
