package com.puyixiaowo.fbook.utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *
 * @author Moses.wei
 * @date 2018-05-12 23:25:43
 * 访问页面工具
 */
public class HtmlUtils {
    private static final Logger logger = LoggerFactory.getLogger(HtmlUtils.class);

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
    public static final String ENCODING = "GBK";
    private static int RETRY_TIMES = 5;
    private static int TIMEOUT = 5 * 1000;

    public static Connection.Response getPage(String url,
                                              String encoding) throws IOException {
        return accessPage(url, Connection.Method.GET, encoding);
    }

    public static Connection.Response postPage(String url, String encoding) throws IOException {
        return accessPage(url, Connection.Method.POST, encoding);
    }

    public static Connection.Response accessPage(String url,
                                                 Connection.Method method,
                                                 String encoding) {

        Connection.Response response = null;
        try {
            Connection connection = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .ignoreContentType(true)
                    .timeout(TIMEOUT)
                    .method(method);

            response = connection.execute();
            response.charset(encoding == null ? ENCODING : encoding);

        } catch (SocketException | SocketTimeoutException e) {
            //重试
            if (RETRY_TIMES > 0) {
                RETRY_TIMES --;
                logger.info("[访问重试]:" + url);
                return accessPage(url, method, encoding);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return response;
    }

}
