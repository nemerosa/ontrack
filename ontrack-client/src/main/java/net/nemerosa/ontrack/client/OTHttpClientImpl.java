package net.nemerosa.ontrack.client;

import net.nemerosa.ontrack.common.Document;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.stripEnd;

public class OTHttpClientImpl implements OTHttpClient {

    private final URL url;
    private final HttpHost host;
    private final Supplier<CloseableHttpClient> httpClientSupplier;
    private final HttpClientContext httpContext;
    private final OTHttpClientLogger clientLogger;

    public OTHttpClientImpl(URL url, HttpHost host, Supplier<CloseableHttpClient> httpClientSupplier, HttpClientContext httpContext, OTHttpClientLogger clientLogger) {
        this.url = url;
        this.host = host;
        this.httpClientSupplier = httpClientSupplier;
        this.httpContext = httpContext;
        this.clientLogger = clientLogger;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    protected String getUrl(String path, Object... parameters) {
        if (StringUtils.startsWith(path, "http")) {
            return format(path, parameters);
        } else {
            String formattedPath = format(path, parameters);
            if (!formattedPath.startsWith("/")) {
                formattedPath = "/" + formattedPath;
            }
            return format(
                    "%s%s",
                    stripEnd(url.toString(), "/"),
                    formattedPath
            );
        }
    }

    @Override
    public <T> T get(ResponseParser<T> responseParser, String path, Object... parameters) {
        return request(new HttpGet(getUrl(path, parameters)), responseParser);
    }

    @Override
    public <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters) {
        return request(new HttpDelete(getUrl(path, parameters)), responseParser);
    }

    @Override
    public <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters) {
        HttpPost post = new HttpPost(getUrl(path, parameters));
        if (data != null) {
            post.setEntity(data);
        }
        return request(post, responseParser);
    }

    @Override
    public <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters) {
        HttpPut put = new HttpPut(getUrl(path, parameters));
        if (data != null) {
            put.setEntity(data);
        }
        return request(put, responseParser);
    }

    @Override
    public <T> T upload(ResponseParser<T> responseParser, String name, Document document, String fileName, String path, Object... parameters) {
        HttpPost post = new HttpPost(getUrl(path));
        // Sets the content
        post.setEntity(
                MultipartEntityBuilder.create()
                        .addBinaryBody(
                                name,
                                document.getContent(),
                                ContentType.parse(document.getType()),
                                fileName
                        )
                        .build()
        );
        // OK
        return request(post, responseParser);
    }

    @Override
    public Document download(String path, Object... parameters) {
        HttpGet get = new HttpGet(getUrl(path));
        return request(get, (request, response, entity) -> {
            // Gets the content as bytes
            byte[] bytes = EntityUtils.toByteArray(entity);
            if (bytes == null || bytes.length == 0) {
                return Document.EMPTY;
            }
            // OK
            return new Document(
                    entity.getContentType().getValue(),
                    bytes
            );
        });
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

    @Override
    public CloseableHttpClient getHttpClient() {
        return httpClientSupplier.get();
    }

    @Override
    public HttpHost getHttpHost() {
        return host;
    }

    @Override
    public HttpClientContext getHttpClientContext() {
        return httpContext;
    }

    protected <T> T request(HttpRequestBase request, ResponseHandler<T> responseHandler) {
        clientLogger.trace("[request] " + request);
        // Executes the call
        try {
            try (CloseableHttpClient http = httpClientSupplier.get()) {
                HttpResponse response = http.execute(host, request, httpContext);
                clientLogger.trace("[response] " + response);
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
        if (statusCode == HttpStatus.SC_OK ||
                statusCode == HttpStatus.SC_CREATED ||
                statusCode == HttpStatus.SC_ACCEPTED) {
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
