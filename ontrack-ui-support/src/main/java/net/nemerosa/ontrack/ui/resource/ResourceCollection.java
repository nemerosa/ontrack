package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.ViewSupplier;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
public class ResourceCollection<T> extends LinkContainer<ResourceCollection<T>> implements ViewSupplier {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Pagination pagination;
    private final Collection<Resource<T>> resources;
    @JsonIgnore
    private final Class<?> viewType;

    protected ResourceCollection(Collection<Resource<T>> resources, URI href, Pagination pagination, Class<?> viewType) {
        super(href);
        this.pagination = pagination;
        this.resources = resources;
        this.viewType = viewType;
    }

    public ResourceCollection<T> forView(Class<?> viewType) {
        return new ResourceCollection<>(resources, getHref(), pagination, viewType);
    }

    public static <R> ResourceCollection<R> of(Stream<Resource<R>> resources, URI href) {
        return of(resources.collect(Collectors.toList()), href);
    }

    public static <R> ResourceCollection<R> of(Collection<Resource<R>> resources, URI href) {
        return new ResourceCollection<>(resources, href, Pagination.NONE, Object.class);
    }
}
