package net.nemerosa.ontrack.boot.resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Supplier;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Link<T> {

    private final String uri;
    private final Supplier<Resource<T>> supplier;
    private final Resource<T> resource;

    public Link(String uri) {
        this(uri, null, null);
    }

    public Link(String uri, Supplier<Resource<T>> supplier) {
        this(uri, supplier, null);
    }

    protected Link(String uri, Resource<T> resource) {
        this(uri, null, resource);
    }

    public Link<T> with(Resource<T> resource) {
        return new Link<>(uri, resource);
    }
}
