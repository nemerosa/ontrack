package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.model.structure.ViewSupplier;

import java.net.URI;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EqualsAndHashCode(callSuper = false)
@Data
public class Resources<T> extends LinkContainer<Resources<T>> implements ViewSupplier {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Wither
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
}
