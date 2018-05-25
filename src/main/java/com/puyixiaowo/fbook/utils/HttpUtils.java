package com.puyixiaowo.fbook.utils;

import com.puyixiaowo.fbook.exception.TimeoutException;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpUtils {

    private static int TIMEOUT_REQUEST = 5000;
    private static int TIMEOUT_CONNECTION = 5000;
    private static int TIMEOUT_READ_DATA = 12000;

    public static HttpUtils readTimeout(int readTimeout) {
        TIMEOUT_READ_DATA = readTimeout;
        return HttpUtils.;
    }

    /**
     * httpPost
     *
     * @param url    路径
     * @param params 参数
     * @return
     */
    public String httpPost(String url, Map<String, String> params, int timeout) throws TimeoutException {
        PostMethod post = new PostMethod(url);
//        post.setRequestHeader("Content-Type",
//                "application/x-www-form-urlencoded;charset=UTF-8");

        if (params != null && params.size() > 0) {
            post.setRequestBody(getParams(params));
        }

        return request(post, timeout);
    }

    public String httpPost(String url, Map<String, String> params,
                                  Map<String, String> headers,
                                  int timeout) throws TimeoutException {
        PostMethod post = new PostMethod(url);

        if (params != null && params.size() > 0) {
            post.setRequestBody(getParams(params));
        }

        if (headers != null && headers.size() > 0) {
            for (Map.Entry entry: headers.entrySet()) {
                if (StringUtils.isBlank(entry.getKey())
                        || StringUtils.isBlank(entry.getValue())) {

                    continue;
                }
                post.setRequestHeader(entry.getKey().toString(), entry.getValue().toString());
            }
        }

        return request(post, timeout);
    }

    /**
     * httpGet
     *
     * @param url    路径
     * @return
     */
    public String httpGet(String url, Map<String, String> params, int timeout) throws TimeoutException {
        GetMethod get = new GetMethod(url);

        if (params != null && params.size() > 0) {

            get.setQueryString(getParams(params));
        }
        return request(get, timeout);
    }


    private NameValuePair [] getParams(Map<String, String> params) {
        List<NameValuePair> nvpList = new ArrayList<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nvpList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        }
        NameValuePair [] arr = new NameValuePair[nvpList.size()];
        return nvpList.toArray(arr);
    }

    private String request(HttpMethod method,
                                  int timeout) {
        method.getParams().setParameter(HttpMethodParams.SO_TIMEOUT,TIMEOUT_REQUEST);
        String str = null;
        HttpClient httpClient = new HttpClient();
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_READ_DATA);
        try {
            InputStream in = null;
            int statusCode = httpClient.executeMethod(method);

            String charset;
            if(method instanceof PostMethod){
                charset = ((PostMethod)method).getResponseCharSet();
            }else{
                charset = ((GetMethod)method).getResponseCharSet();
            }

            if (statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {

                HttpMethod method302 = new GetMethod(method.getResponseHeader("Location").getValue());
                statusCode = httpClient.executeMethod(method302);
                method = method302;
            }

            if (statusCode != HttpStatus.SC_OK) {
                return str;
            }
            in = method.getResponseBodyAsStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
            str = reader.lines().collect(Collectors.joining("\n"));
            if (StringUtils.isBlank(str)) {
                return str;
            }
            return str;
        } catch (IOException e) {
            throw new TimeoutException("请求接口失败:" + e.getMessage());
        }
    }






}
