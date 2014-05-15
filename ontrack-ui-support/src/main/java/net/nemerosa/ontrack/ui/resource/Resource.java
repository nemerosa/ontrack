package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.Container;
import org.apache.commons.lang3.Validate;

import java.beans.ConstructorProperties;
import java.net.URI;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonPropertyOrder(alphabetic = true)
public class Resource<T> extends LinkContainer<Resource<T>> implements Container<T> {

    @JsonUnwrapped
    private final T data;
    @JsonIgnore
    private final Class<T> type;

    @ConstructorProperties({"data", "href"})
    protected Resource(T data, URI href) {
        super(href);
        Validate.notNull(data, "Null data is not acceptable for a resource");
        this.data = data;
        //noinspection unchecked
        this.type = (Class<T>) data.getClass();
    }

    public static <R> Resource<R> of(R data, URI uri) {
        return new Resource<>(data, uri);
    }

}
