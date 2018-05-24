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
import static com.puyixiaowo.fbook.utils.StringUtils.replaceBlank;


/**
 * @author Moses
 * @date 2018-05-17 17:38:11
 * 5hzw爬取规则模版实现
 */

public class FivehzwPickRulesTemplateImpl extends DefaultPickRulesTemplateImpl implements PickRulesTemplate {

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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("q", keywords);
        jsonObject.put("action", "search");
        return jsonObject;
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
            es.add(document.select(".ptm-content a").first().appendChild(document.select(".ptm-content img").first()));
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
        return element.select("a").text();
    }

    @Override
    public String getSearchItemAuthor(Element element) {
        return "未知";
    }

    @Override
    public String getSearchItemCategory(Element element) {
        return "未知";
    }

    @Override
    public String getSearchItemUpdateDate(Element element) {
        return "未知";
    }

    @Override
    public String getSearchItemUpdateChapter(Element element) {
        return "未知";
    }

    @Override
    public String getSearchItemFaceUrl(Element element) {

        return element.select("img").attr("src");
    }

    @Override
    public String getBookEncoding() {
        return "UTF-8";
    }

    @Override
    public String getBookDetailLink(BookBean bookBean) {
        return "https://m.w23us.com/book/" + bookBean.getBookIdThird() + "/";
    }

    @Override
    public String getBookDetailTitle(Document document) {
        return document.select("h1.pt-name").text();
    }

    @Override
    public String getBookDetailAuthor(Document document) {
        return document.select(".pt-info").get(0).text().replace("作者：", "");
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
        return document.select("#chapterlist li");
    }

    @Override
    public String getChapterListLink(BookBean bookBean) {
        return "https://m.w23us.com/book/" + bookBean.getBookIdThird() + "/";
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
