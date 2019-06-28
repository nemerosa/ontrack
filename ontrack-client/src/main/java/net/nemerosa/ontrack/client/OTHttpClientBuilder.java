package net.nemerosa.ontrack.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.function.Supplier;

public class OTHttpClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OTHttpClient.class);

    public static OTHttpClientBuilder create(String url, boolean disableSsl) {
        return new OTHttpClientBuilder(url, disableSsl);
    }

    private final URL url;
    private final boolean disableSsl;
    private final HttpHost host;
    private String username;
    private String password;
    private OTHttpClientLogger clientLogger = logger::debug;
    private int timeoutSeconds = 30;

    protected OTHttpClientBuilder(String url, boolean disableSsl) {
        try {
            this.url = new URL(url);
            this.disableSsl = disableSsl;
        } catch (MalformedURLException e) {
            throw new ClientURLException(url, e);
        }
        this.host = new HttpHost(
                this.url.getHost(),
                this.url.getPort(),
                this.url.getProtocol()
        );
    }

    @SuppressWarnings("unused")
    public OTHttpClientBuilder withLogger(OTHttpClientLogger httpClientLogger) {
        this.clientLogger = httpClientLogger;
        return this;
    }

    public OTHttpClientBuilder withCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    public OTHttpClientBuilder withTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
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

        // SSL setup
        SSLConnectionSocketFactory sslSocketFactory;
        if (disableSsl) {
            logger.warn("Disabling SSL checks!");
            SSLContext ctx;
            try {
                X509TrustManager x509TrustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                };
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[]{x509TrustManager}, new SecureRandom());
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new OTHttpClientSSLSetupException(e);
            }

            sslSocketFactory = new SSLConnectionSocketFactory(
                    ctx,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
            );
        } else {
            sslSocketFactory = SSLConnectionSocketFactory.getSocketFactory();
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();

        // Timeout settings
        RequestConfig.Builder requestConfig = RequestConfig.custom();
        requestConfig.setConnectTimeout(timeoutSeconds * 1000);
        requestConfig.setConnectionRequestTimeout(timeoutSeconds * 1000);
        requestConfig.setSocketTimeout(timeoutSeconds * 1000);

        Supplier<CloseableHttpClient> httpClientSupplier = () -> {
            // Defaults
            HttpClientBuilder builder = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig.build())
                    .setConnectionManager(new PoolingHttpClientConnectionManager(registry));

            // OK
            return builder.build();
        };

        return new OTHttpClientImpl(url, host, httpClientSupplier, httpContext, clientLogger);
    }
}
