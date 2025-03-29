package net.nemerosa.ontrack.client;

import net.nemerosa.ontrack.common.Document;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHost;

import java.net.URL;

/**
 * @deprecated Will be removed in V5. Use the Spring REST Template.
 */
@Deprecated
public interface OTHttpClient {

    URL getUrl();

    /**
     * Gets the URL for a relative path
     *
     * @param path       Relative path
     * @param parameters Parameter for the relative path parameters (if any)
     * @return Absolute URL
     */
    String getUrl(String path, Object... parameters);

    <T> T get(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T upload(ResponseParser<T> responseParser, String name, Document file, String fileName, String path, Object... parameters);

    /**
     * Downloads a document
     */
    Document download(String path, Object... parameters);

    <T> T request(HttpUriRequestBase request, final ResponseParser<T> responseParser);

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
