package net.nemerosa.ontrack.ui.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Collection;

@EqualsAndHashCode(callSuper = false)
@Data
public class ResourceCollection<T> extends Resource<Collection<Resource<T>>> {

    private final Pagination pagination;

}
