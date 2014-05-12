package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.net.URI;

@EqualsAndHashCode(callSuper = false)
@Data
public class Resource<T> extends LinkContainer<Resource<T>> {

    private final T data;

    @ConstructorProperties({"data", "href"})
    protected Resource(T data, URI href) {
        super(href);
        this.data = data;
    }

    public static <R> Resource<R> of(R data, URI uri) {
        return new Resource<>(data, uri);
    }

}
