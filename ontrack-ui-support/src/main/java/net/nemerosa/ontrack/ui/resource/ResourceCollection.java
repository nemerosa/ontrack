package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.ui.support.ResourceCollectionJsonSerializer;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
@JsonSerialize(using = ResourceCollectionJsonSerializer.class)
public class ResourceCollection<T> extends LinkContainer<ResourceCollection<T>> {

    private final Pagination pagination;
    private final Collection<Resource<T>> resources;

    @ConstructorProperties({"resources", "href", "pagination"})
    protected ResourceCollection(Collection<Resource<T>> resources, URI href, Pagination pagination) {
        super(href);
        this.pagination = pagination;
        this.resources = resources;
    }

    public static <R> ResourceCollection<R> of(Stream<Resource<R>> resources, URI href) {
        return of(resources.collect(Collectors.toList()), href);
    }

    public static <R> ResourceCollection<R> of(Collection<Resource<R>> resources, URI href) {
        return new ResourceCollection<>(resources, href, Pagination.NONE);
    }
}
