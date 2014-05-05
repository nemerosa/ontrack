package net.nemerosa.ontrack.boot.resource;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.Map;

@EqualsAndHashCode
@ToString
public class Resource<T> {

    private final T data;
    private final Map<String, Link> links = new LinkedHashMap<>();

    protected Resource(T data) {
        this.data = data;
    }

    public static <T> Resource<T> of(T data) {
        return new Resource<>(data);
    }

    public static Resource<Object> empty() {
        return new Resource<>(null);
    }

    public Resource<T> self(String uri) {
        return link("self", uri);
    }

    public Resource<T> link(String rel, String uri) {
        links.put(rel, new Link(uri));
        return this;
    }

    public T getData() {
        return data;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}
