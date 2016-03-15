package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.nemerosa.ontrack.model.structure.ViewSupplier;

import java.net.URI;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
public class Resources<T> extends LinkContainer<Resources<T>> implements ViewSupplier {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Pagination pagination;
    private final Collection<T> resources;
    @JsonIgnore
    private final Class<?> viewType;

    protected Resources(Collection<T> resources, URI self, Pagination pagination, Class<?> viewType) {
        super(self);
        this.pagination = pagination;
        this.resources = resources;
        this.viewType = viewType;
    }

    public Resources<T> forView(Class<?> viewType) {
        return new Resources<>(resources, get_self(), pagination, viewType);
    }

    public static <R> Resources<R> of(Stream<R> resources, URI href) {
        return of(resources.collect(Collectors.toList()), href);
    }

    public static <R> Resources<R> of(Collection<R> resources, URI href) {
        return new Resources<>(resources, href, Pagination.NONE, Object.class);
    }

    public Resources<T> withPagination(Pagination pagination) {
        return this.pagination == pagination ? this : new Resources<>(this.resources, get_self(), pagination, this.viewType);
    }

    /**
     * Creates a new <code>Resources</code> object by transforming the elements in the underlying collection.
     * The links and the pagination are kept but not the view type.
     */
    public <V> Resources<V> transform(Function<T, V> fn) {
        return new Resources<>(
                resources.stream().map(fn).collect(Collectors.toList()),
                get_self(),
                pagination,
                Object.class
        ).withLinks(getLinks());
    }
}
