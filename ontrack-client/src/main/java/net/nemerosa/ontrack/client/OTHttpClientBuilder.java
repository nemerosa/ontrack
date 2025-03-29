package net.nemerosa.ontrack.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthCache;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.auth.BasicAuthCache;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.BasicScheme;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.Timeout;
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

/**
 * @deprecated Will be removed in V5. Use the Spring REST Template instead.
 */
@Deprecated
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
    private int timeoutSeconds = 5 * 60;

    protected OTHttpClientBuilder(String url, boolean disableSsl) {
        try {
            this.url = new URL(url);
            this.disableSsl = disableSsl;
        } catch (MalformedURLException e) {
            throw new ClientURLException(url, e);
        }
        this.host = new HttpHost(
                this.url.getProtocol(),
                this.url.getHost(),
                this.url.getPort()
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

            BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(
                    new AuthScope(host),
                    new UsernamePasswordCredentials(username, password.toCharArray())
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
                    NoopHostnameVerifier.INSTANCE
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
        requestConfig.setConnectTimeout(Timeout.ofSeconds(timeoutSeconds));
        requestConfig.setConnectionRequestTimeout(Timeout.ofSeconds(timeoutSeconds));
        requestConfig.setResponseTimeout(Timeout.ofSeconds(timeoutSeconds));

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
