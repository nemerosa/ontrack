package net.nemerosa.ontrack.dsl.http

import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.AuthCache
import org.apache.http.client.CredentialsProvider
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.*
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.net.ssl.KeyManager
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

class OTHttpClientBuilder {

    private static final Logger logger = LoggerFactory.getLogger(OTHttpClient)

    private final URL url
    private final boolean disableSsl
    private final HttpHost host
    private String username
    private String password
    private int maxTries = 1
    private int retryDelaySeconds = 10
    private Closure clientLogger = { message -> println message }

    OTHttpClientBuilder(String url, boolean disableSsl) {
        this.url = new URL(url)
        this.disableSsl = disableSsl
        this.host = new HttpHost(
                this.url.host,
                this.url.port,
                this.url.protocol
        )
    }

    OTHttpClientBuilder withLogger(Closure httpClientLogger) {
        this.clientLogger = httpClientLogger
        return this
    }

    OTHttpClientBuilder withMaxTries(int value) {
        this.maxTries = value
        return this
    }

    OTHttpClientBuilder withRetryDelaySeconds(int value) {
        this.retryDelaySeconds = value
        return this
    }

    OTHttpClientBuilder withCredentials(String username, String password) {
        this.username = username
        this.password = password
        return this
    }

    public OTHttpClient build() {

        HttpClientContext httpContext = HttpClientContext.create()

        // Basic authentication
        if (username && password) {

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider()
            credentialsProvider.setCredentials(
                    new AuthScope(host),
                    new UsernamePasswordCredentials(username, password)
            )

            AuthCache authCache = new BasicAuthCache()
            authCache.put(host, new BasicScheme())

            httpContext.credentialsProvider = credentialsProvider
            httpContext.authCache = authCache
        }

        httpContext.cookieStore = new BasicCookieStore()

        // SSL setup
        SSLConnectionSocketFactory sslSocketFactory
        if (disableSsl) {
            logger.warn "Disabling SSL checks!"
            SSLContext ctx
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
            }
            ctx = SSLContext.getInstance("TLS")
            ctx.init(new KeyManager[0], [x509TrustManager] as TrustManager[], new SecureRandom())

            sslSocketFactory = new SSLConnectionSocketFactory(
                    ctx,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
            )
        } else {
            logger.debug "Using default secure SSL socket factory."
            sslSocketFactory = SSLConnectionSocketFactory.socketFactory
        }

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build()

        Closure<CloseableHttpClient> httpClientSupplier = {
            HttpClientBuilder.create()
                    .setConnectionManager(new PoolingHttpClientConnectionManager(registry)).build()
        }

        new OTHttpClient(url, host, httpClientSupplier, httpContext, clientLogger, maxTries, retryDelaySeconds)
    }
}
