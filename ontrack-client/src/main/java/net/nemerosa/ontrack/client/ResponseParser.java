package net.nemerosa.ontrack.client;

import java.io.IOException;

@FunctionalInterface
public interface ResponseParser<T> {

    T parse(String content) throws IOException;

}
