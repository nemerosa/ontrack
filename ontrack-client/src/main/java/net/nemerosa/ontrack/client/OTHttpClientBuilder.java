package net.nemerosa.ontrack.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

public class OTHttpClientBuilder {

    public static OTHttpClientBuilder create(String url) {
        return new OTHttpClientBuilder(url);
    }

    private final URL url;
    private final HttpHost host;
    private String username;
    private String password;

    protected OTHttpClientBuilder(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            throw new ClientURLException(url, e);
        }
        this.host = new HttpHost(
                this.url.getHost(),
                this.url.getPort(),
                this.url.getProtocol()
        );
    }

    public OTHttpClientBuilder withCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public OTHttpClient build() {

        HttpClientContext httpContext = HttpClientContext.create();

        // Basic authentication
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(host),
                    new UsernamePasswordCredentials(username, password)
            );

            AuthCache authCache = new BasicAuthCache();
            authCache.put(host, new BasicScheme());

            httpContext.setCredentialsProvider(credentialsProvider);
            httpContext.setAuthCache(authCache);
        }

        CookieStore cookieStore = new BasicCookieStore();
        httpContext.setCookieStore(cookieStore);

        Supplier<CloseableHttpClient> httpClientSupplier = () -> {
            // Defaults
            HttpClientBuilder builder = HttpClientBuilder.create()
                    .setConnectionManager(new PoolingHttpClientConnectionManager());

            // OK
            return builder.build();
        };

        return new OTHttpClientImpl(url, host, httpClientSupplier, httpContext);
    }
}
