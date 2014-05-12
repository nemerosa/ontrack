package net.nemerosa.ontrack.ui.resource;

import lombok.Data;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class Resource<T> {

    private final T data;
    private final URI href;
    private final Map<String, Link> links = new LinkedHashMap<>();

    public static <R> Resource<R> of(R data, URI uri) {
        return new Resource<>(data, uri);
    }

    public Resource<T> with(String name, URI uri) {
        return with(Link.of(name, uri));
    }

    private Resource<T> with(Link link) {
        links.put(link.getName(), link);
        return this;
    }

}
