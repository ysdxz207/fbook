package com.puyixiaowo.fbook.service.book;

import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

public class Test2 implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private static final Spider spider = Spider.create(new Test2());

    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static Document getPage(String url) {
        spider.addUrl(url).thread(5);
        return spider.get(url);
    }

    public static void main(String[] args) {
        Document document = getPage("http://www.mxguan.com/book/840/8360558.html");
        System.out.println(document.html());
    }
}
