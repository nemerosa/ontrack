package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Collection;

@EqualsAndHashCode(callSuper = false)
@Data
public class ResourceCollection<T> extends Resource<Collection<Resource<T>>> {

    private final Pagination pagination;

    @ConstructorProperties({"data", "href", "pagination"})
    public ResourceCollection(Collection<Resource<T>> data, URI href, Pagination pagination) {
        super(data, href);
        this.pagination = pagination;
    }
}
