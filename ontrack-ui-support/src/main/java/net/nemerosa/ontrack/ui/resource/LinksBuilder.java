package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

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

    LinksBuilder entityURI(String name, ProjectEntity projectEntity, boolean allowed);

    LinksBuilder link(String name, Object methodInvocation, Class<? extends GlobalFunction> fn);

    LinksBuilder link(String name, Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId);

    default LinksBuilder link(String name, Object methodInvocation, Class<? extends ProjectFunction> fn, ProjectEntity projectEntity) {
        return link(name, methodInvocation, fn, projectEntity.projectId());
    }

    LinksBuilder page(String name, String path, Object... arguments);

    LinksBuilder page(String name, boolean allowed, String path, Object... arguments);

    LinksBuilder entityPage(String name, boolean allowed, ProjectEntity projectEntity);

    default LinksBuilder page(ProjectEntity projectEntity) {
        return entityPage(Link.PAGE, true, projectEntity);
    }

    LinksBuilder page(String name, Class<? extends GlobalFunction> fn, String path, Object... arguments);

    LinksBuilder page(String name, Class<? extends ProjectFunction> fn, ProjectEntity projectEntity, String path, Object... arguments);

    LinksBuilder update(Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId);

    LinksBuilder delete(Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId);

    List<Link> build();

}
