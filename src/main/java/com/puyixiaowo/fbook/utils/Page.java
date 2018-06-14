package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 *
 * @author Moses.wei
 * @date 2018-06-13 17:44:35
 *
 * 通用HTTP请求工具
 */

public class Page {

    private static final Logger logger = LoggerFactory.getLogger(Page.class);

    private static int TIMEOUT_REQUEST = 5000;
    private static int TIMEOUT_CONNECTION = 5000;
    private static int TIMEOUT_READ_DATA = 12000;
    private static int RETRY_TIMES = 0;
    private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";

    private static String CHARSET = "UTF-8";

    private static boolean IGNORE_USER_AGENT = false;

    private static final Pattern PATTERN_CHARSET = Pattern.compile(".*charset=([^;]*).*");
    private static final Pattern PATTERN_CHARSET_DEEP = Pattern.compile(".*charset=\"(.*)\".*");

    public static Page create() {
        return new Page();
    }

    public Page readTimeout(int readTimeout) {
        TIMEOUT_READ_DATA = readTimeout;
        return this;
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

    public Page ignoreUserAgent(boolean ignoreUserAgent) {
        IGNORE_USER_AGENT = ignoreUserAgent;
        return this;
    }

    public Page postCharset(String charset) {
        if (StringUtils.isBlank(charset)) {
            CHARSET = charset;
        }
        return this;
    }

    private HttpRequestBase getMethod(String url,
                                 String method,
                                 Object params) {

        HttpRequestBase httpMethod;

        switch (method) {
            default:
            case "GET":

                httpMethod = buildGetMethod(url, params);
                break;
            case "POST":

                httpMethod = buildPostMethod(url, params);
                break;
        }


        return httpMethod;
    }

    private HttpRequestBase buildGetMethod(String url, Object params) {
        if (params == null) {
            return new HttpGet(url);
        }
        if (!(params instanceof JSONObject)) {
            throw new RuntimeException("Get method paramaters should be JSONObject.");
        }
        JSONObject paramsJSON = (JSONObject) params;
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (!paramsJSON.isEmpty()) {
                builder.addParameters(getParams(paramsJSON));
            }
            return new HttpGet(builder.build());
        } catch (URISyntaxException e) {
            throw new RuntimeException("[Page get parameters exception]:" + paramsJSON.toJSONString());
        }
    }

    private HttpRequestBase buildPostMethod(String url,
                                       Object params) {
        HttpPost post = new HttpPost(url);

        if (params == null) {
            return post;
        }

        if (params instanceof JSONObject) {
            JSONObject paramsJSON = (JSONObject) params;
            if (!paramsJSON.isEmpty()) {

                UrlEncodedFormEntity paramsEntity;
                try {
                    paramsEntity = new UrlEncodedFormEntity(getParams(paramsJSON), CHARSET);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("[Page post parameters exception]:" + paramsJSON.toJSONString());
                }
                post.setEntity(paramsEntity);
            }
        } else if (params instanceof String) {
            StringEntity stringEntity = new StringEntity(params.toString(), CHARSET);
            post.setEntity(stringEntity);
        } else {
            throw new RuntimeException("Unsupported paramaters type.");
        }
        return post;
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
     *      GET方式支持JSONObject类型
     *      POST方式支持JSONObject和String类型
     * @param method
     * @return
     */
    public Response read(String url,
                          Object params,
                          Connection.Method method) {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(TIMEOUT_CONNECTION)
                .setConnectionRequestTimeout(TIMEOUT_REQUEST)
                .setSocketTimeout(TIMEOUT_READ_DATA)
                .build();

        Response response = new Response();
        if (StringUtils.isBlank(url)) {
            return response;
        }

        HttpRequestBase httpMethod = getMethod(url, method.name(), params);
        HttpClientContext context = HttpClientContext.create();
        if (!IGNORE_USER_AGENT) {
            httpMethod.addHeader("User-Agent", USER_AGENT);
        }
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
        return response;
    }

    private Response requestAndParse(HttpClient httpClient,
                                     HttpRequestBase method,
                                     HttpClientContext context) throws IOException {
        Response response = new Response();
        Document document = new Document("");
        response.setDocument(document);
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
        document.setBaseUri(baseUri);

        byte[] bytes = EntityUtils.toByteArray(httpResponse.getEntity());
        String html = new String(bytes);

        if (statusCode == HttpStatus.SC_OK
                && StringUtils.isNotBlank(html)) {
            String charset = getCharset(Jsoup.parse(html));
            html = new String(bytes, charset);
            if (StringUtils.isNotBlank(html)) {
                org.jsoup.nodes.Document doc = Jsoup.parse(html);
                if (doc == null) {
                    return response;
                }
                response.setDocument(doc);
                return response;
            }
        }

        return response;
    }

    private String getCharset(org.jsoup.nodes.Document document) {
        boolean deep = false;
        Elements eles = document.select("meta[http-equiv=Content-Type]");

        if (eles.size() == 0) {
            deep = true;
            eles = document.select("meta");
        }
        for (Element element : eles) {
            Matcher m;
            if (!deep) {
                m = PATTERN_CHARSET.matcher(element.attr("content"));
            } else {
                m = PATTERN_CHARSET_DEEP.matcher(element.toString());
            }
            if (m.find()) {
                return m.group(1);
            }
        }


        return CHARSET;
    }

    static class Response {

        private Document document;

        public Response() {
            this.document = new Document("");
        }

        public Response(Document document) {
            this.document = document == null ? new Document("") : document;
        }

        public Object bodyToJSON() throws NotJsonException{
            Object result = null;
            try {
                result = JSON.parse(document.text());
            } catch (Exception ignored) {
            }
            if (result == null) {
                throw new NotJsonException("Body is not json.");
            }
            return result;
        }

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }
    }

    private static class NotJsonException extends RuntimeException {

        public NotJsonException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {

        JSONObject jsonObject = JSON.parseObject("{\"gmt_create\":[\"2018-06-01 14:00:18\"],\"charset\":[\"UTF-8\"],\"seller_email\":[\"18011453383\"],\"subject\":[\"镰刀-腾冲店-扫码付\"],\"sign\":[\"W9QzWWmyYQaLBMUZRyn6y5/IOQSJdpS+Mm2xueBGFcpRE2A1tjbhRG5rENUKL2zs+oxWkxqs75Uo2Q7Xq2bZDvgro4fz+rn/0B4gHY4uRZi1CSNiHKMASFmigCqbvt6Ccr6VK2Z+sImvjbVk4v0V0jPUFBHXJnOE+AknOyOqLzo=\"],\"buyer_id\":[\"2088502919031107\"],\"invoice_amount\":[\"0.00\"],\"notify_id\":[\"46e3f83dcf2ab2e0a141ac56de2a44dgrx\"],\"fund_bill_list\":[\"[{\\\"amount\\\":\\\"0.02\\\",\\\"fundChannel\\\":\\\"COUPON\\\"}]\"],\"notify_type\":[\"trade_status_sync\"],\"trade_status\":[\"TRADE_SUCCESS\"],\"receipt_amount\":[\"0.02\"],\"buyer_pay_amount\":[\"0.02\"],\"app_id\":[\"2016072901681821\"],\"sign_type\":[\"RSA\"],\"seller_id\":[\"2088522946013721\"],\"gmt_payment\":[\"2018-06-01 14:00:22\"],\"notify_time\":[\"2018-06-01 14:00:23\"],\"version\":[\"1.0\"],\"out_trade_no\":[\"18060114001341210225700\"],\"total_amount\":[\"0.02\"],\"trade_no\":[\"2018060121001004100542988229\"],\"auth_app_id\":[\"2015122501042113\"],\"buyer_logon_id\":[\"135***@qq.com\"],\"point_amount\":[\"0.02\"]}");

        JSONObject params = new JSONObject();
        for (Map.Entry entry:
             jsonObject.entrySet()) {
            String key = entry.getKey().toString();
            Object value = ((JSONArray) entry.getValue()).get(0).toString();
            params.put(key, value);
        }
        Response response = Page.create().read("http://puyixiaowo.win/test/json",
                params, Connection.Method.POST);
        System.out.println(JSON.toJSONString(response.bodyToJSON()));
    }
}
