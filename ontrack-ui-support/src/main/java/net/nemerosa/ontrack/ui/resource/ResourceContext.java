package net.nemerosa.ontrack.ui.resource;

import java.net.URI;

public interface ResourceContext {

    /**
     * @see net.nemerosa.ontrack.ui.controller.URIBuilder#build(Object)
     */
    URI uri(Object methodInvocation);

}
