package net.nemerosa.ontrack.ui.resource;

public abstract class AbstractResourceDecorator<T> implements ResourceDecorator<T> {

    private final Class<T> resourceClass;

    protected AbstractResourceDecorator(Class<T> resourceClass) {
        this.resourceClass = resourceClass;
    }

    @Override
    public boolean appliesFor(Class<?> beanClass) {
        return resourceClass.isAssignableFrom(beanClass);
    }
}
