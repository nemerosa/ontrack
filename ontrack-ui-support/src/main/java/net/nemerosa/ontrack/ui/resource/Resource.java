package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.Container;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Optional;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonPropertyOrder(alphabetic = true)
public class Resource<T> extends LinkContainer<Resource<T>> implements Container<T> {

    @JsonUnwrapped
    private final T data;

    @ConstructorProperties({"data", "href"})
    protected Resource(T data, URI href) {
        super(href);
        this.data = data;
    }

    public static <R> Resource<R> of(R data, URI uri) {
        return new Resource<>(data, uri);
    }

    @Override
    public Optional<T> first() {
        return Optional.ofNullable(data);
    }
}
