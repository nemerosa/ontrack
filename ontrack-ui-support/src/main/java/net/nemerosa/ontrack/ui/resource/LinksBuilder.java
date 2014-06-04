package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;

import java.net.URI;
import java.util.List;

/**
 * @see net.nemerosa.ontrack.ui.controller.URIBuilder
 */
public interface LinksBuilder {

    LinksBuilder link(Link link);

    LinksBuilder link(String name, URI uri);

    LinksBuilder self(Object methodInvocation);

    LinksBuilder link(String name, Object methodInvocation);

    LinksBuilder link(String name, Object methodInvocation, boolean test);

    LinksBuilder link(String name, Object methodInvocation, Class<? extends GlobalFunction> fn);

    LinksBuilder link(String name, Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId);

    LinksBuilder update(Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId);

    List<Link> build();

}
