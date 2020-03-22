package net.nemerosa.ontrack.ui.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public interface ResourceDecorator<T> {

    default List<Link> links(T resource, ResourceContext resourceContext) {
        return Collections.emptyList();
    }

    default @Nullable
    Link linkByName(@NotNull T resource, @NotNull ResourceContext resourceContext, @NotNull String linkName) {
        return null;
    }

    boolean appliesFor(Class<?> beanClass);

    /**
     * This method is called to give this decorator an opportunity to change the content of the model object
     * before it is serialized. A typical use is the obfuscation of sensitive data before it is sent to the client.
     * By default, this method returns the same bean.
     *
     * @param bean Model object
     * @return Decorated object
     */
    default T decorateBeforeSerialization(T bean) {
        return bean;
    }

    /**
     * Gets the list of link names this resource decorator can provide.
     */
    default List<String> getLinkNames() {
        return Collections.emptyList();
    }

}
