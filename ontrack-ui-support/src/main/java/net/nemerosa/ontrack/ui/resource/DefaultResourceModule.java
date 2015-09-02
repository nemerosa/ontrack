package net.nemerosa.ontrack.ui.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class DefaultResourceModule implements ResourceModule {

    private final Collection<ResourceDecorator<?>> decorators;

    @Autowired
    public DefaultResourceModule(Collection<ResourceDecorator<?>> decorators) {
        this.decorators = decorators;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return decorators;
    }
}
