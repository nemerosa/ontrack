package net.nemerosa.ontrack.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

public class HttpClientImpl implements HttpClient {

    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);
    private final String url;
    private final HttpHost host;
    private HttpClientContext httpContext = HttpClientContext.create();

    public HttpClientImpl(ClientConnection connection) throws MalformedURLException {
        this.url = connection.getUrl();
        URL u = new URL(connection.getUrl());
        this.host = new HttpHost(
                u.getHost(),
                u.getPort(),
                u.getProtocol()
        );
    }

    protected CloseableHttpClient http() {
        return httpBuilder().build();
    }

    protected HttpClientBuilder httpBuilder() {
        return HttpClientBuilder.create().setConnectionManager(new PoolingHttpClientConnectionManager());
    }

    protected String getUrl(String path, Object... parameters) {
        return format("%s%s", url, format(path, parameters));
    }

    @Override
    public <T> T get(ResponseParser<T> responseParser, String path, Object... parameters) {
        return request(new HttpGet(getUrl(path, parameters)), responseParser);
    }

    @Override
    public <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser) {
        return request(
                request,
                (request1, response, entity) -> baseHandleResponse(request1, response, entity,
                        entity1 -> {
                            // Gets the content as a string
                            String content = entity1 != null ? EntityUtils.toString(entity1, "UTF-8") : null;
                            // Parses the response
                            return responseParser.parse(content);
                        })
        );
    }

    protected <T> T request(HttpRequestBase request, ResponseHandler<T> responseHandler) {
        logger.debug("[request] {}", request);
        // Executes the call
        try {
            try (CloseableHttpClient http = http()) {
                HttpResponse response = http.execute(host, request, httpContext);
                logger.debug("[response] {}", response);
                // Entity response
                HttpEntity entity = response.getEntity();
                try {
                    return responseHandler.handleResponse(request, response, entity);
                } finally {
                    EntityUtils.consume(entity);
                }
            }
        } catch (IOException e) {
            throw new ClientGeneralException(request, e);
        } finally {
            request.releaseConnection();
        }
    }

    protected <T> T baseHandleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity,
                                       EntityParser<T> entityParser) throws ParseException, IOException {
        // Parses the response
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            return entityParser.parse(entity);
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            throw new ClientValidationException(getMessage(response));
        } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            throw new ClientCannotLoginException(request);
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            throw new ClientForbiddenException(request);
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            throw new ClientNotFoundException(getMessage(response));
        } else if (statusCode == HttpStatus.SC_NO_CONTENT) {
            return null;
        } else if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            String content = getMessage(response);
            if (StringUtils.isNotBlank(content)) {
                throw new ClientMessageException(content);
            } else {
                // Generic error
                throw new ClientServerException(
                        request,
                        statusCode,
                        response.getStatusLine().getReasonPhrase());
            }
        } else {
            // Generic error
            throw new ClientServerException(
                    request,
                    statusCode,
                    response.getStatusLine().getReasonPhrase());
        }
    }

    private static String getMessage(HttpResponse response) throws IOException {
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    @FunctionalInterface
    protected static interface EntityParser<T> {

        T parse(HttpEntity entity) throws IOException;

    }

    @FunctionalInterface
    protected static interface ResponseHandler<T> {

        T handleResponse(HttpRequestBase request, HttpResponse response, HttpEntity entity) throws ParseException, IOException;

    }

}
