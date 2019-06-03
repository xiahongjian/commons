package tech.hongjian.commons.net;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class RequestBuilder {
    private String charset = "UTF-8";
    private String url;
    private List<Header> headers = new ArrayList<>();
    private String body;
    private Map<String, List<String>> params = new HashMap<>();
    private String username;
    private String password;
    private int timeout = 30000;

    public RequestBuilder(String url) {
        this.url = url;
    }

    public RequestBuilder url(@NonNull String url) {
        this.url = url;
        return this;
    }

    public RequestBuilder header(@NonNull String name, @NonNull String value) {
        headers.add(new BasicHeader(name, value));
        return this;
    }

    public RequestBuilder charset(@NonNull String charset) {
        this.charset = charset;
        return this;
    }

    public RequestBuilder urlencodedBody(@NonNull String body) {
        this.body = body;
        this.headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE,
                HttpClientUtil.CONTENT_TYPE_URLENCODED));
        return this;
    }

    public RequestBuilder jsonBody(@NonNull String body) {
        this.body = body;
        this.headers.add(new BasicHeader(HttpHeaders.CONTENT_TYPE,
                HttpClientUtil.CONTENT_TYPE_JSON));
        return this;
    }

    public RequestBuilder param(@NonNull String name, @NonNull String value) {
        List<String> values;
        if (params.containsKey(name)) {
            values = params.get(name);
        } else {
            values = new ArrayList<>();
            params.put(name, values);
        }
        values.add(value);
        return this;
    }

    public RequestBuilder param(@NonNull String name, @NonNull List<String> values) {
        List<String> vs;
        if (params.containsKey(name)) {
            vs = params.get(name);
            vs.addAll(values);
        } else {
            vs = new ArrayList<>(values.size());
            vs.addAll(values);
            params.put(name, vs);
        }
        return this;
    }

    public RequestBuilder param(@NonNull String name, String... values) {
        if (values == null || values.length == 0) {
            return this;
        }
        return param(name, Arrays.asList(values));
    }

    public RequestBuilder auth(@NonNull String username, @NonNull String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public RequestBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public RestRequest build() {
        RestRequest req = new RestRequest();
        req.charset = charset;
        req.url = url;
        req.headers = headers;
        req.body = body;
        req.params = params;
        req.timeout = timeout;
        if (username != null && password != null) {
            req.headers.add(new BasicHeader(HttpHeaders.AUTHORIZATION,
                    HttpClientUtil.authorization(username, password)));
        }
        return req;
    }


    private static class RestRequest {
        String charset;
        String url;
        List<Header> headers;
        String body;
        Map<String, List<String>> params;
        int timeout;

        public String get(String url) throws Exception {
            if (url != null) {
                this.url = url;
            }
            URI uri =
                    new URIBuilder(url).addParameters(params.entrySet().stream().flatMap(e -> e.getValue().stream().map(v -> new BasicNameValuePair(e.getKey(), v))).collect(Collectors.toList())).build();
            HttpGet get = new HttpGet(uri);
            get.setHeaders(headers.toArray(new Header[headers.size()]));
            get.setConfig(HttpClientUtil.buildRequestConfig(timeout));
            return HttpClientUtil.doRequest(get, charset);
        }

        public String get() throws Exception {
            return get(null);
        }

        public String post(String url) throws Exception {
            if (url != null) {
                this.url = url;
            }
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(body, charset));
            post.setConfig(HttpClientUtil.buildRequestConfig(timeout));
            return HttpClientUtil.doRequest(post, charset);
        }
    }

}
