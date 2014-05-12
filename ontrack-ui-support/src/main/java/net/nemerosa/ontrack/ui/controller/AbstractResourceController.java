package net.nemerosa.ontrack.ui.controller;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

public abstract class AbstractResourceController {

    @Context
    protected UriInfo uriInfo;

    protected UriBuilder uriBuilder() {
        return uriBuilder(getClass());
    }

    protected UriBuilder uriBuilder(String method) {
        return uriBuilder(getClass(), method);
    }

    protected UriBuilder uriBuilder(Class<?> clz) {
        return UriBuilder.fromUri(uriInfo.getBaseUri()).path(clz);
    }

    protected UriBuilder uriBuilder(Class<?> clz, String method) {
        return UriBuilder.fromUri(uriInfo.getBaseUri()).path(clz, method);
    }
}
