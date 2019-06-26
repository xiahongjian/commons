package tech.hongjian.commons.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import tech.hongjian.commons.json.JSONUtil;

/**
 * @author xiahongjian
 */
@Slf4j
public class HttpClientUtil {

    private static PoolingHttpClientConnectionManager connManager = null;
    private static CloseableHttpClient httpClient = null;
    private final static int CONNECT_TIMEOUTS = 30000;
    private final static int KEEP_ALIVE_MILLSEC = 30000;

    public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    private static final String DEFAULT_ENCODING = "UTF-8";

    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";


    // 初始化
    static {
        try {
            // 获得一个SSLContext实例
            SSLContext sslContext = SSLContext.getInstance("TLS");
            // sslContext初始化时指定信任管理器（此处未修改内部方法，绕过本地验证）
            sslContext.init(null, new TrustManager[] {new X509TrustManager() {
                // 返回受信任的X509证书数组
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                // 该方法检查客户端的证书，若不信任该证书则抛出异常。
                // 我们不需要对客户端进行认证，所以只需要执行默认的信任管理器的这个方法。
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                // 该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的任何证书。
                // 在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }}, null);

            // 注册不同连接方式（http、https）的socket工厂
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", new SSLConnectionSocketFactory(sslContext)).build();

            ConnectionKeepAliveStrategy keepAliveStrategy =
                    new DefaultConnectionKeepAliveStrategy() {
                        @Override
                        public long getKeepAliveDuration(HttpResponse response,
                                HttpContext context) {
                            long keepAlive = super.getKeepAliveDuration(response, context);
                            if (keepAlive == -1) {
                                // Keep connections alive KEEP_ALIVE milliseconds if a
                                // keep-alive value has not be explicitly set by the
                                // server
                                keepAlive = KEEP_ALIVE_MILLSEC;
                            }
                            return keepAlive;
                        }
                    };

            // 获得一个连接池管理实例
            connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            httpClient = HttpClients.custom().setConnectionManager(connManager)
                    .setKeepAliveStrategy(keepAliveStrategy).build();
            // 创建socket配置实例
            SocketConfig socketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            connManager.setDefaultSocketConfig(socketConfig);
            // 创建message配置实例
            MessageConstraints messageConstraints = MessageConstraints.custom()
                    .setMaxHeaderCount(200).setMaxLineLength(2000).build();
            // 创建连接池配置实例
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setMalformedInputAction(CodingErrorAction.IGNORE)
                    .setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Consts.UTF_8)
                    .setMessageConstraints(messageConstraints).build();
            connManager.setDefaultConnectionConfig(connectionConfig);
            connManager.setMaxTotal(200);
            connManager.setDefaultMaxPerRoute(20);
        } catch (KeyManagementException e) {
            log.error("KeyManagementException", e);
        } catch (NoSuchAlgorithmException e) {
            log.error("NoSuchAlgorithmException", e);
        }
    }

    /**
     * http get method实现（无需身份验证则usernam和password传入null）
     *
     * @param url 请求地址
     * @param encode 返回参数编码格式
     * @param timeout 链接超时时间
     * @param username 用户名
     * @param password 密码
     * @return 请求返回数据字符串
     */
    public static String get(String url, Map<String, String> params, String encode, int timeout,
            String username, String password) throws Exception {
        URI uri = new URIBuilder(setProtocol(url)).addParameters(params.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList())).build();
        HttpGet get = new HttpGet(uri);
        setAuth(get, username, password);
        get.setConfig(buildRequestConfig(timeout == 0 ? CONNECT_TIMEOUTS : timeout));
        log.info("Begin to execute GET request，url: {}, param(s): {}", url,
                JSONUtil.toJSON(params));
        try {
            String res = doRequest(get, encode);
            log.info("Finish executing GET request, url: {}, param(s): {}, response: " + "{}", url,
                    params, res);
            return res;
        } catch (Exception e) {
            log.error("Failed to execute GET request, url: {}", url, e);
            throw e;
        }
    }

    public static String get(String url, Map<String, String> params, String username,
            String password) throws Exception {
        return get(url, params, DEFAULT_ENCODING, CONNECT_TIMEOUTS, username, password);
    }

    public static String get(String url, Map<String, String> params) throws Exception {
        return get(url, params, DEFAULT_ENCODING, CONNECT_TIMEOUTS, null, null);
    }

    public static String get(String url, String username, String password) throws Exception {
        return get(url, Collections.emptyMap(), DEFAULT_ENCODING, CONNECT_TIMEOUTS, username,
                password);
    }

    public static String get(String url) throws Exception {
        return get(url, Collections.emptyMap(), DEFAULT_ENCODING, CONNECT_TIMEOUTS, null, null);
    }


    /**
     * http Post Method实现（无需用户名密码则传入null）
     *
     * @param url 请求地址
     * @param timeout 链接超时时间
     * @param postData post请求的数据
     * @param contentType 请求数据类型
     * @param encoding 返回数据编码格式
     * @return 返回数据结果字符串
     */
    public static String post(String url, int timeout, String postData, String contentType,
            String encoding, String userName, String password) throws Exception {
        HttpPost post = new HttpPost(url);
        setAuth(post, userName, password);
        post.setHeader(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON);
        post.setHeader(HttpHeaders.CONTENT_TYPE, contentType);
        post.setConfig(buildRequestConfig(timeout == 0 ? CONNECT_TIMEOUTS : timeout));

        if (postData != null) {
            post.setEntity(new StringEntity(postData, encoding));
        }
        log.info("Begin to execute POST request, url: {}, param(s): {}", url, postData);
        try {
            String str = doRequest(post, encoding);
            log.info("Finish executing POST request, url: {}, response: {}", url, str);
            return str;
        } catch (IOException e) {
            log.error("Failed to execute POST request, url: {}, param(s): {}", url, postData, e);
            throw e;
        }
    }

    public static String doRequest(HttpRequestBase request, String encoding) throws IOException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String str = handleResponse(response, encoding);
            return str;
        } catch (IOException e) {
            throw e;
        } finally {
            request.releaseConnection();
        }
    }

    public static String post(String url, String data, String contentType, String username,
            String password) throws Exception {
        return post(url, CONNECT_TIMEOUTS, data, contentType, DEFAULT_ENCODING, username, password);
    }

    public static String post(String url, String data, String contentType) throws Exception {
        return post(url, CONNECT_TIMEOUTS, data, contentType, DEFAULT_ENCODING, null, null);
    }

    public static String post(String url, String data) throws Exception {
        return post(url, CONNECT_TIMEOUTS, data, CONTENT_TYPE_URLENCODED, DEFAULT_ENCODING, null,
                null);
    }

    public static String post(String url, String data, String username, String password)
            throws Exception {
        return post(url, CONNECT_TIMEOUTS, data, CONTENT_TYPE_URLENCODED, DEFAULT_ENCODING,
                username, password);
    }

    public static String post(String url) throws Exception {
        return post(url, CONNECT_TIMEOUTS, null, CONTENT_TYPE_URLENCODED, DEFAULT_ENCODING, null,
                null);
    }


    /**
     * http Put Method实现（无需用户名密码则传入null）
     *
     * @param url 请求地址
     * @param timeout 链接超时时间
     * @param data post请求的数据
     * @param contentType 请求数据类型
     * @param encoding 返回数据编码格式
     * @param username 身份验证用户名
     * @param password 身份验证密码
     * @return 返回数据结果字符串
     */
    public static String put(String url, int timeout, String data, String contentType,
            String encoding, String username, String password) throws Exception {
        HttpPut put = new HttpPut(url);
        setAuth(put, username, password);
        put.setHeader(HttpHeaders.ACCEPT, CONTENT_TYPE_JSON);
        put.setHeader(HttpHeaders.CONTENT_TYPE, contentType);

        RequestConfig requestConfig = buildRequestConfig(timeout == 0 ? CONNECT_TIMEOUTS : timeout);
        put.setConfig(requestConfig);
        if (data != null) {
            put.setEntity(new StringEntity(data, encoding));
        }
        log.info("Begin to execute PUT request, url: {}, param(s): {}", url, data);
        try {
            String res = doRequest(put, encoding);
            log.info("Finish executing PUT request, url: {}, param(s): {}, response: " + "{}", url,
                    data, res);
            return res;
        } catch (IOException e) {
            log.error("Failed to execute PUT request, url: {}, param(s): {}", url, data, e);
            throw e;
        }
    }


    /**
     * 设置信任自签名证书（设置需要证书时使用，初始化SSLContext实例时需要配置证书存放路径，需本地证书验证方法预留）
     *
     * @param keyStorePath 密钥库路径
     * @param keyStorePassword 密钥库密码
     * @return
     */
    public static SSLContext custom(String keyStorePath, String keyStorePassword) {
        SSLContext sc = null;
        try (FileInputStream in = new FileInputStream(new File(keyStorePath))) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(in, keyStorePassword.toCharArray());
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy())
                    .build();
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | KeyManagementException e) {
            e.printStackTrace();
        }
        return sc;
    }


    private static void setAuth(HttpRequest req, String username, String password) {
        if ((username != null) || (password != null)) {
            req.setHeader(HttpHeaders.AUTHORIZATION, authorization(username, password));
        }
    }

    public static RequestConfig buildRequestConfig(int timeout) {
        return RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout)
                .setConnectionRequestTimeout(timeout).setExpectContinueEnabled(false).build();
    }

    private static String handleResponse(CloseableHttpResponse response, String encoding)
            throws IOException {
        if (!isOK(response)) {
            return response.getStatusLine().toString();
        }
        HttpEntity entity = response.getEntity();
        try {
            if (entity != null) {
                return EntityUtils.toString(entity, encoding);
            }
        } finally {
            if (entity != null) {
                entity.getContent().close();
            }
        }
        return null;
    }

    // 返回状态码结果判断
    public static boolean isOK(CloseableHttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200;
    }

    /**
     * 把map中的元素，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 参数
     * @return 拼接后字符串
     */
    public static String getQueryString(Map<String, String> params, String encoding) {
        return URLEncodedUtils.format(params.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList()), encoding);
    }

    public static String getQueryString(Map<String, String> params) {
        return getQueryString(params, DEFAULT_ENCODING);
    }

    /**
     * base auth
     *
     * @param username 用户名
     * @param password 密码
     * @return base auth字符数
     */
    public static String authorization(String username, String password) {
        return "Basic " + Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes(Consts.UTF_8));
    }
    
    public static String setProtocol(@NonNull String url) {
        if (url.startsWith(HTTP_PREFIX) || url.startsWith(HTTPS_PREFIX))
            return url;
        return HTTP_PREFIX + url;
    }
}
