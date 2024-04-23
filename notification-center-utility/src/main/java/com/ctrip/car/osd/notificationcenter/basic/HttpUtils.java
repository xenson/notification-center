package com.ctrip.car.osd.notificationcenter.basic;

import com.ctrip.car.osd.notificationcenter.config.QCAppsetting;
import com.ctrip.car.osd.notificationcenter.log.LogUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.ning.http.client.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import qunar.hc.QHttpOption;
import qunar.hc.QunarAsyncClient;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zmxie on 2018/1/31.
 */
public class HttpUtils {

    private static final CloseableHttpClient httpClient;
    private static final String WEB_PROXY = "WEB_PROXY";
    private static final String REQUEST_TIME_OUT = "REQUEST_TIME_OUT";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_TIMEOUT = 60000;
    private static final int HTTP_OK = 200;
    private static PoolingHttpClientConnectionManager cm = null;

    static {
        LayeredConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault());
        } catch (NoSuchAlgorithmException e) {
            LogUtils.error("NoSuchAlgorithmException", e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        cm = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        cm.setMaxTotal(400);
        cm.setDefaultMaxPerRoute(cm.getMaxTotal());

        httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();
    }

    // 无代理和请求头设置的get请求
    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, new HashMap<>(), new HashMap<>(), DEFAULT_CHARSET);
    }

    // 无参数的get请求
    public static String doGet(String url, Map<String, String> headers, Map<String, String> config) throws IOException {
        return doGet(url, new HashMap<>(), headers, config, DEFAULT_CHARSET);
    }

    // 带参数和请求头以及代理设置的get请求
    public static String doGet(String url, Map<String, String> params, Map<String, String> headers, Map<String, String> config) throws IOException {
        return doGet(url, params, headers, config, DEFAULT_CHARSET);
    }

    // 无请求头以及代理设置的post请求 (key-value参数)
    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, new HashMap<>(), new HashMap<>(), DEFAULT_CHARSET);
    }

    // 无请求头以及代理设置的post请求 (content参数)
    public static String doPost(String url, String contents) throws IOException {
        return doPost(url, contents, new HashMap<>(), new HashMap<>(), DEFAULT_CHARSET);
    }

    // 启用默认代理的http
    public static String doPost(String url, String contents, boolean useDefaultProxy) throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put(WEB_PROXY, QCAppsetting.get(WEB_PROXY));
        config.put(REQUEST_TIME_OUT, String.valueOf(DEFAULT_TIMEOUT));
        return doPost(url, contents, new HashMap<>(), config, DEFAULT_CHARSET);
    }

    // 带参数和请求头以及代理设置的post请求 (content)参数
    public static String doPost(String url, String contents, Map<String, String> headers, Map<String, String> config) throws IOException {
        return doPost(url, contents, headers, config, DEFAULT_CHARSET);
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers, Map<String, String> config, String charset)
            throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            // 设置url参数
            if (params != null && !params.isEmpty()) {
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            HttpGet httpGet = new HttpGet(url);
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    Header header = new BasicHeader(entry.getKey(), entry.getValue());
                    httpGet.addHeader(header);
                }
            }
            HttpHost proxy = null;
            if (config != null && !config.isEmpty()) {
                if (!StringUtils.isBlank(config.get(WEB_PROXY))) {
                    proxy = new HttpHost(config.get(WEB_PROXY).split(":")[0], Integer.valueOf(config.get(WEB_PROXY).split(":")[1]));
                }
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(Integer.valueOf(config.get(REQUEST_TIME_OUT)))
                        .setConnectTimeout(Integer.valueOf(config.get(REQUEST_TIME_OUT)))
                        .setProxy(proxy)
                        .build();
                httpGet.setConfig(requestConfig);
            }
            CloseableHttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                httpGet.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw e;
        }
    }


    public static String doPost(String url, String contents, Map<String, String> headers, Map<String, String> config,
                                String charset) throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpHost proxy = null;
            if (null != config && !config.isEmpty()) {
                if (!StringUtils.isBlank(config.get(WEB_PROXY))) {
                    proxy = new HttpHost(config.get(WEB_PROXY).split(":")[0],
                            Integer.valueOf(config.get(WEB_PROXY).split(":")[1]));
                }
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(Integer.valueOf(config.get(REQUEST_TIME_OUT)))
                        .setConnectTimeout(Integer.valueOf(config.get(REQUEST_TIME_OUT)))
                        .setProxy(proxy)
                        .build();
                httpPost.setConfig(requestConfig);
            }

            if (null != headers && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    Header header = new BasicHeader(entry.getKey(), entry.getValue());
                    httpPost.addHeader(header);
                }
            }
            httpPost.setEntity(new StringEntity(contents, DEFAULT_CHARSET));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    public static String doPost(String url, Map<String, String> params, Map<String, String> headers, Map<String, String> config, String charset) throws IOException {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        try {
            List<NameValuePair> pairs = null;
            if (params != null && !params.isEmpty()) {
                pairs = new ArrayList<NameValuePair>(params.size());
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    String value = entry.getValue();
                    if (value != null) {
                        pairs.add(new BasicNameValuePair(entry.getKey(), value));
                    }
                }
            }
            HttpPost httpPost = new HttpPost(url);
            if (pairs != null && pairs.size() > 0) {
                httpPost.setEntity(new UrlEncodedFormEntity(pairs, DEFAULT_CHARSET));
            }
            // 设置代理以及超时相关
            if (null != config && !config.isEmpty()) {
                HttpHost proxy = null;
                if (!StringUtils.isBlank(config.get(WEB_PROXY))) {
                    proxy = new HttpHost(config.get(WEB_PROXY).split(":")[0],
                            Integer.valueOf(config.get(WEB_PROXY).split(":")[1]));
                }
                int timeout = DEFAULT_TIMEOUT;
                if (!StringUtils.isBlank(config.get(REQUEST_TIME_OUT))) {
                    timeout = Integer.valueOf(config.get(REQUEST_TIME_OUT));
                }
                RequestConfig requestConfig = RequestConfig.custom()
                        .setSocketTimeout(timeout)
                        .setConnectTimeout(timeout)
                        .setProxy(proxy)
                        .build();
                httpPost.setConfig(requestConfig);
            }
            // 设置请求头
            if (null != headers && !headers.isEmpty()) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    Header header = new BasicHeader(entry.getKey(), entry.getValue());
                    httpPost.addHeader(header);
                }
            }
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HTTP_OK) {
                httpPost.abort();
                throw new RuntimeException("HttpClient,error status code :" + statusCode);
            }
            HttpEntity entity = response.getEntity();
            String result = null;
            if (entity != null) {
                result = EntityUtils.toString(entity, DEFAULT_CHARSET);
            }
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    public static String postBrief(String url, String contents) {
        String result = "";
        HttpPost httpRequst = new HttpPost(url);// 创建HttpPost对象
        try {
            StringEntity entity = new StringEntity(contents);
            entity.setContentType("application/json");
            entity.setContentEncoding("UTF-8");
            httpRequst.setEntity(entity);
            HttpHost proxy = getProxy();
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
            httpRequst.setConfig(requestConfig);

            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequst);
            //System.out.println(httpResponse.getStatusLine().getStatusCode());
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);// 取出应答字符串
                //System.out.println(result);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (httpRequst != null) {
                httpRequst.abort();
            }
        }
        return result;
    }

    public static byte[] getBrief(String url) {
        byte[] bytes = null;
        HttpGet httpRequst = new HttpGet(url);// 创建HttpGet对象
        try {
            HttpHost proxy = getProxy();
            if (null != proxy) {
                RequestConfig requestConfig = null;
                requestConfig = RequestConfig.custom()
                        .setProxy(proxy)
                        .build();
                httpRequst.setConfig(requestConfig);
            }
            HttpResponse httpResponse = httpClient.execute(httpRequst);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                bytes = EntityUtils.toByteArray(httpEntity);
            }

            if (bytes == null || bytes.length == 0) {
                LogUtils.error("getBrief failed", url + "\n" + JsonUtils.parseJson(httpResponse));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (httpRequst != null) {
                httpRequst.abort();
            }
        }
        return bytes;
    }

    public static HttpHost getProxy() {
        HttpHost httpHost = null;
        String proxy = QCAppsetting.get(WEB_PROXY);
        if (StringUtils.isNotBlank(proxy)) {
            String s[] = proxy.split(":");
            if (s.length == 2) {
                httpHost = new HttpHost(s[0], Integer.valueOf(s[1]));
            }
        }
        return httpHost;
    }


    /**
     * do post by ctrip ProxyGate2
     * post json only:body,post form only:form
     * http://conf.ctripcorp.com/pages/viewpage.action?pageId=168850661
     *
     * @param url
     * @param body
     * @param form
     * @return
     */
    public static String doPostProxy(String url, String body, Map<String, String> form) {
        String res = "";
        try {
            QunarAsyncClient qunarAsyncClient = new QunarAsyncClient();
            QHttpOption option = new QHttpOption();
            option.addHeader("Content-Type", "application/json; charset=utf-8");
            if (StringUtils.isEmpty(body) && form != null) {
                option.addHeader("Content-Type", "application/x-www-form-urlencoded");

                for (Map.Entry<String, String> param : form.entrySet()) {
                    option.addPostFormData(param.getKey(), param.getValue());
                }
            } else {
                option.setPostBodyData(body);
            }

            ListenableFuture<Response> responseFuture = qunarAsyncClient.post(url, option);
            Response response = responseFuture.get();
            if (response != null && response.getStatusCode() == 200) {
                res = response.getResponseBody("UTF-8");
            } else {
                String statusCode = response != null ? String.valueOf(response.getStatusCode()) : "null";
                LogUtils.warn("HttpUtils_doPostProxy", statusCode + "\n" + url + "\n" + body + "\n" + JsonUtils.parseJson(form));
            }
        } catch (Exception ex) {
            LogUtils.error("HttpUtils_doPostProxy", url + "\n" + body + "\n" + JsonUtils.parseJson(form));
        }
        return res;
    }

    /**
     * post form construct body string
     *
     * @param params
     * @return
     */
    private static String getPostBody(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                result.append("&");
            } catch (Exception ex) {

            }
        }
        return result.substring(0, result.length() - 1).toString();
    }

    private static QunarAsyncClient qunarClient = new QunarAsyncClient();

    /**
     * do post by ctrip ProxyGate2
     *
     * @param url
     * @return
     */
    public static String doGetProxy(String url) {
        String res = "";
        try {
            qunarClient.setConnectionTimeoutInMs(60000);
            qunarClient.setRequestTimeoutInMs(180000);
            ListenableFuture<Response> responseFuture = qunarClient.get(url);
            Response response = responseFuture.get();
            if (response != null && response.getStatusCode() == 200) {
                res = response.getResponseBody("UTF-8");
            }
        } catch (Exception ex) {
            LogUtils.error("HttpUtils_doPostProxy", url);
        }
        return res;
    }

}
