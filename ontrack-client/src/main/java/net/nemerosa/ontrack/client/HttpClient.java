package net.nemerosa.ontrack.client;

import org.apache.http.client.methods.HttpRequestBase;

public interface HttpClient {

    <T> T get(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser);

}
