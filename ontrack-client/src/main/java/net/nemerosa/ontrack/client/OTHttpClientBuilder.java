package net.nemerosa.ontrack.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

public class OTHttpClientBuilder {

    private static final CookieStore cookieStore = new BasicCookieStore();

    public static OTHttpClientBuilder create(String url) {
        return new OTHttpClientBuilder(url);
    }

    private final URL url;
    private final HttpHost host;
    private String username;
    private String password;
    private boolean trustAnyCertificate;

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

    public OTHttpClientBuilder withTrustAnyCertificate(boolean trust) {
        this.trustAnyCertificate = trust;
        return this;
    }

    public OTHttpClient build() {
        // Defaults
        HttpClientContext httpContext = HttpClientContext.create();
        HttpClientBuilder builder = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager());

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
            httpContext.setCookieStore(cookieStore);

            // TODO Associates with the HTTP client
        }

        // SSL context
        if (trustAnyCertificate) {
            try {
                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        new SSLContextBuilder()
                                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                                .build()
                );
                builder.setSSLSocketFactory(sslsf);
            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                throw new ClientCannotConfigureSSLException(e);
            }
        }

        // OK
        CloseableHttpClient httpClient = builder.build();
        return new OTHttpClientImpl(url, host, httpClient, httpContext);
    }
}
