package net.nemerosa.ontrack.ui.resource;

import java.util.Collection;

public interface ResourceModule {

    Collection<ResourceDecorator<?>> decorators();

}
