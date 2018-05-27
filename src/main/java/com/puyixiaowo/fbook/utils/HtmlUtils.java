package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Moses.wei
 * @date 2018-05-12 23:25:43
 * 访问页面工具
 */
public class HtmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    public static final String USER_AGENT_PC = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    public static final String USER_AGENT_PHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_1 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Mobile/14A403 Safari/602.1";

    private static final Page page = Page.getInstance()
            .retryTimes(3)
            .readTimeout(10 * 1000)
            .connectionTimeout(5 * 1000)
            .requestTimeout(5 * 1000);

    public static Document getPage(String url) {
        return accessPage(url, null, Connection.Method.GET, "PC");
    }

    public static Document getPage(String url,
                                   String device) {
        return accessPage(url, null, Connection.Method.GET, device);
    }

    public static Document getPage(String url, JSONObject params, String device) {
        return accessPage(url, params, Connection.Method.GET, device);
    }

    public static Document postPage(String url, JSONObject params) {
        return accessPage(url, params, Connection.Method.POST, "PC");
    }

    public static Document postPage(String url, JSONObject params, String device) {
        return accessPage(url, params, Connection.Method.POST, device);
    }

    public static Document accessPage(String url,
                                      JSONObject params,
                                      Connection.Method method,
                                      String device) {

        switch (device) {
            default:
            case "PC":
                page.userAgent(USER_AGENT_PC);
                break;
            case "PHONE":
                page.userAgent(USER_AGENT_PHONE);
                break;
        }

        switch (method) {
            default:
            case GET:
            return page.read(url, params, Connection.Method.GET);
            case POST:
            return page.read(url, params, Connection.Method.POST);
        }
    }

    public static void main(String[] args) throws IOException {

        Document doc = getPage("http://www.mxguan.com/book/840/8360558.html", "");

        System.out.println(doc.html());
    }
}
