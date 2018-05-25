package com.puyixiaowo.fbook.service.book;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class BaiduBaikePageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    public BaiduBaikePageProcessor() {
    }

    public void process(Page page) {
        page.putField("name", page.getHtml().css("dl.lemmaWgt-lemmaTitle h1", "text").toString());
        page.putField("description", page.getHtml().xpath("//div[@class='lemma-summary']/allText()"));
    }

    public Site getSite() {
        return this.site;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new BaiduBaikePageProcessor()).thread(2);
        String urlTemplate = "http://baike.baidu.com/search/word?word=%s&pic=1&sug=1&enc=utf8";
        ResultItems resultItems = (ResultItems)spider.get(String.format(urlTemplate, "水力发电"));
        System.out.println(resultItems);
        List<String> list = new ArrayList();
        list.add(String.format(urlTemplate, "风力发电"));
        list.add(String.format(urlTemplate, "太阳能"));
        list.add(String.format(urlTemplate, "地热发电"));
        list.add(String.format(urlTemplate, "地热发电"));
        List<ResultItems> resultItemses = spider.getAll(list);
        Iterator var6 = resultItemses.iterator();

        while(var6.hasNext()) {
            ResultItems resultItemse = (ResultItems)var6.next();
            System.out.println(resultItemse.getAll());
        }

        spider.close();
    }
}