package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.ViewSupplier;
import org.apache.commons.lang3.Validate;

import java.beans.ConstructorProperties;
import java.net.URI;

/**
 * The resource wrapper class might be used for UI return types whenever:
 * <ul>
 * <li>a model object is not applicable, when describing an API for example</li>
 * <li>the links cannot be derived from the model object because the resource
 * maybe served by different controllers</li>
 * </ul>
 *
 * @param <T> Type of data to wrap into the resource
 */
@EqualsAndHashCode(callSuper = false)
@Data
@JsonPropertyOrder(alphabetic = true)
public class Resource<T> extends LinkContainer<Resource<T>> implements ViewSupplier {

    @JsonUnwrapped
    private final T data;
    @JsonIgnore
    private final Class<T> viewType;

    @ConstructorProperties({"data", "_self"})
    protected Resource(T data, URI self) {
        super(self);
        Validate.notNull(data, "Null data is not acceptable for a resource");
        this.data = data;
        //noinspection unchecked
        this.viewType = (Class<T>) data.getClass();
    }

    public static <R> Resource<R> of(R data, URI self) {
        return new Resource<>(data, self);
    }

}
