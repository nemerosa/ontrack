package net.nemerosa.ontrack.client;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpRequestBase;

import java.net.URL;

public interface OTHttpClient {

    URL getUrl();

    <T> T get(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T delete(ResponseParser<T> responseParser, String path, Object... parameters);

    <T> T post(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T put(ResponseParser<T> responseParser, HttpEntity data, String path, Object... parameters);

    <T> T request(HttpRequestBase request, final ResponseParser<T> responseParser);

}
