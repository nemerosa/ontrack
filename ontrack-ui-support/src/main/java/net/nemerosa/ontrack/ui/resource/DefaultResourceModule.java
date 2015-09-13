package net.nemerosa.ontrack.ui.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DefaultResourceModule implements ResourceModule {

    private Collection<ResourceDecorator<?>> decorators;

    @SuppressWarnings("unused")
    public DefaultResourceModule() {
    }

    public DefaultResourceModule(Collection<ResourceDecorator<?>> decorators) {
        this.decorators = decorators;
    }

    @Autowired(required = false)
    public void setDecorators(Collection<ResourceDecorator<?>> decorators) {
        this.decorators = decorators;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return decorators;
    }
}
