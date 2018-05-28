package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSONObject;
import com.sun.jndi.toolkit.url.Uri;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Page {

    private static final Logger logger = LoggerFactory.getLogger(Page.class);

    private static int TIMEOUT_REQUEST = 5000;
    private static int TIMEOUT_CONNECTION = 5000;
    private static int TIMEOUT_READ_DATA = 12000;
    private static int RETRY_TIMES = 0;
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";


    public static Page getInstance() {

        return PageEnum.INSTANCE.singleton;
    }

    private enum PageEnum {
        INSTANCE;

        PageEnum() {
            singleton = new Page();
        }

        private Page singleton;
    }

    public Page readTimeout(int readTimeout) {
        TIMEOUT_READ_DATA = readTimeout;
        return getInstance();
    }

    public Page requestTimeout(int requestTimeout) {
        TIMEOUT_REQUEST = requestTimeout;
        return this;
    }

    public Page connectionTimeout(int connectionTimeout) {
        TIMEOUT_CONNECTION = connectionTimeout;
        return this;
    }

    public Page retryTimes(int n) {
        RETRY_TIMES = n;
        return this;
    }

    public Page userAgent(String userAgent) {
        if (StringUtils.isNotBlank(userAgent)) {
            USER_AGENT = userAgent;
        }
        return this;
    }

    private HttpRequestBase getMethod(String url,
                                 String method,
                                 JSONObject params) {

        HttpRequestBase httpMethod;

        switch (method) {
            default:
            case "GET":
                URIBuilder builder;
                try {
                    builder = new URIBuilder(url);
                    if (params != null
                            && !params.isEmpty()) {
                        builder.addParameters(Page.getInstance().getParams(params));
                    }
                    httpMethod = new HttpGet(builder.build());
                } catch (URISyntaxException e) {
                    throw new RuntimeException("[Page get parameters exception]:" + params.toJSONString());
                }

                break;
            case "POST":
                HttpPost post = new HttpPost(url);
                StringEntity strEntity;
                try {
                    strEntity = new StringEntity(params.toJSONString());
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("[Page post parameters exception]:" + params.toJSONString());
                }
                post.setEntity(strEntity);
                httpMethod = post;
                break;
        }


        if (params == null
                || params.isEmpty()) {
            return httpMethod;
        }

        return httpMethod;
    }

    private List<NameValuePair> getParams(JSONObject params) {

        List<NameValuePair> list = new ArrayList<>();
        if (params == null
            || params.isEmpty()) {
            return list;
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            list.add(new BasicNameValuePair(entry.getKey(), entry.getValue() == null ? null : entry.getValue().toString()));
        }
        return list;
    }

    /**
     *
     * @param url
     * @param params
     * @param method
     * @return
     */
    public Document read(String url,
                          JSONObject params,
                          Connection.Method method) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_CONNECTION)
                .setConnectionRequestTimeout(TIMEOUT_REQUEST)
                .setSocketTimeout(TIMEOUT_READ_DATA)
                .build();

        Document document = new Document("");
        if (StringUtils.isBlank(url)) {
            return document;
        }

        HttpRequestBase httpMethod = getMethod(url, method.name(), params);
        HttpClientContext context = HttpClientContext.create();
        httpMethod.addHeader("User-Agent", USER_AGENT);
        HttpClient httpClient = HttpClients.custom()
                .setRedirectStrategy(new LaxRedirectStrategy())
                .setDefaultRequestConfig(requestConfig).build();

        try {
            return requestAndParse(httpClient, httpMethod, context);

        } catch (IOException e) {
            if (RETRY_TIMES > 0) {
                RETRY_TIMES--;
                logger.info("[Page request retry]:" + url);
                try {
                    return requestAndParse(httpClient, httpMethod, context);
                } catch (IOException e1) {
                }
            }
        }
        return document;
    }

    private Document requestAndParse(HttpClient httpClient,
                                     HttpRequestBase method,
                                     HttpClientContext context) throws IOException {
        HttpResponse httpResponse = httpClient.execute(method, context);

        int statusCode = httpResponse.getStatusLine().getStatusCode();

        HttpHost target = context.getTargetHost();
        List<URI> redirectLocations = context.getRedirectLocations();
        URI location = null;
        try {
            location = URIUtils.resolve(method.getURI(), target, redirectLocations);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String baseUri = location != null ? location.toASCIIString() : "";

        byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
        String html = new String(bytes);

        if (statusCode == HttpStatus.SC_OK
                && StringUtils.isNotBlank(html)) {
            String charset = getCharset(Jsoup.parse(html));
            html = new String(bytes, charset);
            if (StringUtils.isNotBlank(html)) {
                Document document = Jsoup.parse(html);
                document.setBaseUri(baseUri);
                return document;
            }
        }

        return new Document(baseUri);
    }

    public String getCharset(Document document) {
        boolean deep = false;
        Elements eles = document.select("meta[http-equiv=Content-Type]");

        if (eles.size() == 0) {
            deep = true;
            eles = document.select("meta");
        }
        Iterator<Element> it = eles.iterator();
        while (it.hasNext()) {
            Element element = it.next();
            Matcher m;
            if (!deep) {
                Pattern p = Pattern.compile(".*charset=([^;]*).*");
                m = p.matcher(element.attr("content"));
            } else {
                Pattern p = Pattern.compile(".*charset=\"(.*)\".*");
                m = p.matcher(element.toString());
            }
            if (m.find()) {
                return m.group(1);
            }
        }


        return "UTF-8";
    }
}
