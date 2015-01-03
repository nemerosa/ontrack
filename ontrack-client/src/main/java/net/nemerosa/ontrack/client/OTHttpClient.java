package net.nemerosa.ontrack.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;

import java.net.URL;

public interface OTHttpClient {

    URL getUrl();

    <T> T get(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser);

    /**
     * Underlying HTTP client
     */
    CloseableHttpClient getHttpClient();

    /**
     * HTTP host
     */
    HttpHost getHttpHost();

    /**
     * HTTP call context
     */
    HttpClientContext getHttpClientContext();

}
