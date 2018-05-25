package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Moses.wei
 * @date 2018-05-12 23:25:43
 * 访问页面工具
 */
public class HtmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    public static final String USER_AGENT_PC = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    public static final String USER_AGENT_PHONE = "Mozilla/5.0 (iPhone; CPU iPhone OS 10_0_1 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) Version/10.0 Mobile/14A403 Safari/602.1";
    public static final String ENCODING = "GBK";
    private static int RETRY_TIMES = 5;
    private static int TIMEOUT = 5 * 1000;

    public static Connection.Response getPage(String url,
                                              String encoding) {
        return accessPage(url, null, Connection.Method.GET, encoding, "PC");
    }
    public static Connection.Response getPage(String url,
                                               String encoding,
                                               String device) {
        return accessPage(url, null, Connection.Method.GET, encoding, device);
    }

    public static Connection.Response getPage(String url, JSONObject params, String encoding, String device) {
        return accessPage(url, params, Connection.Method.GET, encoding, device);
    }

    public static Connection.Response postPage(String url, JSONObject params, String encoding) {
        return accessPage(url, params, Connection.Method.POST, encoding, "PC");
    }

    public static Connection.Response postPage(String url, JSONObject params, String encoding, String device) {
        return accessPage(url, params, Connection.Method.POST, encoding, device);
    }

    public static Connection.Response accessPage(String url,
                                                 JSONObject params,
                                                 Connection.Method method,
                                                 String encoding,
                                                 String device) {

        Connection.Response response = null;
        try {
            switch (device) {
                default:
                case "PC":
                    device = USER_AGENT_PC;
                    break;
                case "PHONE":
                    device = USER_AGENT_PHONE;
                    break;
            }

            Connection connection = Jsoup.connect(url)
                    .userAgent(device)
                    .ignoreContentType(true)
                    .timeout(TIMEOUT)
                    .method(method);

            if (params != null
                    && params.size() > 0) {
                connection.data(JSON.toJavaObject(params, Map.class));
            }

            response = connection.execute();
            response.charset(encoding == null ? ENCODING : encoding);

        } catch (SocketException | SocketTimeoutException e) {
            //重试
            if (RETRY_TIMES > 0) {
                RETRY_TIMES --;
                logger.info("[访问重试]:" + url);
                return accessPage(url, params, method, encoding, device);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;
    }

    public static void main(String[] args) throws IOException {


        Connection connection = Jsoup.connect("http://www.5hzw.com/8_8114/6150352.html")
                .userAgent(USER_AGENT_PHONE)
                .ignoreContentType(true)
                .timeout(TIMEOUT)
                .method(Connection.Method.GET);

        Connection.Response res = connection.execute();
        Document document = res.parse();
        System.out.println(document);

    }
}
