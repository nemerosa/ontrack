package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.ui.controller.URIBuilder;

import java.net.URI;

public class DefaultResourceContext implements ResourceContext {

    private final URIBuilder uriBuilder;

    public DefaultResourceContext(URIBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    @Override
    public URI uri(Object methodInvocation) {
        return uriBuilder.build(methodInvocation);
    }
}
