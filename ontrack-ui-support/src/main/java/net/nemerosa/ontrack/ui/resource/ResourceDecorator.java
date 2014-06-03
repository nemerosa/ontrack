package net.nemerosa.ontrack.ui.resource;

import java.util.Collections;
import java.util.List;

public interface ResourceDecorator<T> {

    default List<Link> links(T resource, ResourceContext resourceContext) {
        return Collections.emptyList();
    }

    boolean appliesFor(Class<?> beanClass);

}
