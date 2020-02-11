package net.nemerosa.ontrack.ui.support;

import net.nemerosa.ontrack.model.structure.SearchProvider;
import net.nemerosa.ontrack.ui.controller.URIBuilder;

import java.net.URI;

@Deprecated
public abstract class AbstractSearchProvider implements SearchProvider {

    protected final URIBuilder uriBuilder;

    protected AbstractSearchProvider(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    protected URI uri(Object methodInvocation) {
        return uriBuilder.build(methodInvocation);
    }
}
