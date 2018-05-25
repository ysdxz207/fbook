package com.puyixiaowo.fbook.service.book;

import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class Test2 implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    private static final Spider spider = Spider.create(new Test2());

    @Override
    public void process(Page page) {
        page.putField("document", page.getHtml().getDocument());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static Document getPage(String url) {
        ResultItems resultItems = spider.addUrl(url).thread(5).get(url);
        Document document = (Document) resultItems.getAll().get("document");
        spider.close();
        return document;
    }

}
