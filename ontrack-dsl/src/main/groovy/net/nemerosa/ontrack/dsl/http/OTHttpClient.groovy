package net.nemerosa.ontrack.dsl.http

import groovy.json.JsonSlurper
import net.jodah.failsafe.Failsafe
import net.jodah.failsafe.RetryPolicy
import net.nemerosa.ontrack.dsl.Document
import org.apache.http.HttpEntity
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.*
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.util.EntityUtils
import org.codehaus.groovy.runtime.InvokerInvocationException

import java.util.concurrent.TimeUnit

class OTHttpClient {

    private final URL url
    private final HttpHost host
    private final Closure<CloseableHttpClient> httpClientSupplier
    private final HttpClientContext httpContext
    private final Closure clientLogger
    private final RetryPolicy retryPolicy

    OTHttpClient(URL url, HttpHost host, Closure<CloseableHttpClient> httpClientSupplier, HttpClientContext httpContext, Closure clientLogger, int maxTries, int retryDelaySeconds) {
        this(
                url,
                host,
                httpClientSupplier,
                httpContext,
                clientLogger,
                createDefaultRetryPolicy()
                        .withDelay(retryDelaySeconds, TimeUnit.SECONDS)
                        .withMaxRetries(maxTries)
        )
    }

    static RetryPolicy createDefaultRetryPolicy() {
        new RetryPolicy().retryOn({ th ->
            if (th instanceof ConnectException) {
                return true
            } else if (th instanceof InvokerInvocationException) {
                def cause = th.cause
                return (cause instanceof ConnectException)
            } else {
                return false
            }
        })
    }

    OTHttpClient(URL url, HttpHost host, Closure<CloseableHttpClient> httpClientSupplier, HttpClientContext httpContext, Closure clientLogger, RetryPolicy retryPolicy) {
        this.url = url
        this.host = host
        this.httpClientSupplier = httpClientSupplier
        this.httpContext = httpContext
        this.clientLogger = clientLogger
        this.retryPolicy = retryPolicy
    }

    URL getUrl() {
        url
    }

    protected String getUrl(String path) {
        if (path ==~ /^http.*/) {
            path
        } else {
            String formattedPath = path
            if (!formattedPath.startsWith("/")) {
                formattedPath = "/" + formattedPath
            }
            return "${url}${formattedPath}"
        }
    }

    def get(String path, Closure responseParser) {
        request(new HttpGet(getUrl(path)), responseParser)
    }

    def delete(String path, Closure responseParser) {
        request(new HttpDelete(getUrl(path)), responseParser);
    }

    def post(String path, HttpEntity data, Closure responseParser) {
        HttpPost post = new HttpPost(getUrl(path));
        if (data != null) {
            post.entity = data;
        }
        request(post, responseParser);
    }

    def put(String path, HttpEntity data, Closure responseParser) {
        HttpPut put = new HttpPut(getUrl(path));
        if (data != null) {
            put.entity = data;
        }
        request(put, responseParser);
    }

    def upload(String path, String name, String fileName, Document document, Closure responseParser) {
        HttpPost post = new HttpPost(getUrl(path))
        // Sets the content
        post.entity = MultipartEntityBuilder.create().addBinaryBody(
                name,
                document.content,
                ContentType.parse(document.type),
                fileName
        ).build()
        // OK
        request(post, responseParser);
    }

    Document download(String path) {
        HttpGet get = new HttpGet(getUrl(path))
        doRequest(get, { request, response, HttpEntity entity ->
            // Gets the content as bytes
            byte[] bytes = EntityUtils.toByteArray(entity)
            if (bytes == null || bytes.length == 0) {
                return Document.EMPTY;
            }
            // OK
            new Document(
                    entity.contentType.value,
                    bytes
            )
        }) as Document
    }

    Object request(HttpRequestBase request, Closure responseParser) {
        doRequest(
                request,
                { req, resp, HttpEntity entity ->
                    baseHandleResponse(req, resp, entity) { HttpEntity entity1 ->
                        // Gets the content as a string
                        String content = entity1 != null ? EntityUtils.toString(entity1, "UTF-8") : null
                        // Parses the response
                        return responseParser(content)
                    }
                }
        )
    }

    protected Object baseHandleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity,
                                        Closure entityParser) {
        int statusCode = response.statusLine.statusCode;
        clientLogger "[ontrack][response] ${request} CODE ${statusCode} ${response.statusLine.reasonPhrase}"
        if (statusCode == HttpStatus.SC_OK ||
                statusCode == HttpStatus.SC_CREATED ||
                statusCode == HttpStatus.SC_ACCEPTED) {
            return entityParser(entity)
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            throw new OTMessageClientException(getMessage(response))
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new OTNotAuthorizedException()
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            throw new OTForbiddenClientException()
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new OTNotFoundException(getMessage(response))
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            return null
        } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            String content = getMessage(response)
            if (content) {
                throw new OTMessageClientException(content)
            } else {
                // Generic error
                throw new OTHttpClientException(
                        "Error while accessing ${request}: ${statusCode} - ${response.statusLine.reasonPhrase}"
                )
            }
        } else {
            // Generic error
            throw new OTHttpClientException(
                    "Error while accessing ${request}: ${statusCode} - ${response.statusLine.reasonPhrase}"
            )
        }
    }

    private static String getMessage(HttpResponse response) throws IOException {
        def s = EntityUtils.toString(response.entity, "UTF-8")
        if (response.entity.contentType.value.startsWith('application/json')) {
            def json = new JsonSlurper().parseText(s)
            return json['message'] ?: s
        } else {
            return s
        }
    }

    protected Object doRequest(HttpRequestBase request, Closure responseHandler) {
        clientLogger "[ontrack][request] ${request}"
        // Executes the call
        try {
            CloseableHttpClient http = httpClientSupplier()
            try {
                HttpResponse response = Failsafe.with(retryPolicy).get {
                    http.execute(host, request, httpContext)
                }
                // Entity response
                HttpEntity entity = response.entity
                try {
                    return responseHandler(request, response, entity)
                } finally {
                    EntityUtils.consume(entity)
                }
            } finally {
                http.close()
            }
        } finally {
            request.releaseConnection()
        }
    }

    /**
     * Underlying HTTP client
     */
    CloseableHttpClient getHttpClient() {
        httpClientSupplier()
    }

    /**
     * HTTP host
     */
    HttpHost getHttpHost() {
        httpHost
    }

    /**
     * HTTP call context
     */
    HttpClientContext getHttpClientContext() {
        httpClientContext
    }
}
