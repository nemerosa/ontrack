package net.nemerosa.ontrack.boot.resource;

import java.lang.reflect.Method;

public class Resource<T> {

    private final T data;

    protected Resource(T data) {
        this.data = data;
    }

    public static Resource<Object> empty() {
        return new Resource<>(null);
    }

    public Resource link(Method method) {
        // FIXME Method net.nemerosa.ontrack.boot.resource.Resource.link
        return null;
    }
}
