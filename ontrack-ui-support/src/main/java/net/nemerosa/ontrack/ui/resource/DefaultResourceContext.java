package net.nemerosa.ontrack.ui.resource;

import net.nemerosa.ontrack.model.security.GlobalFunction;
import net.nemerosa.ontrack.model.security.ProjectFunction;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.ui.controller.URIBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DefaultResourceContext implements ResourceContext {

    private final URIBuilder uriBuilder;
    private final SecurityService securityService;

    public DefaultResourceContext(URIBuilder uriBuilder, SecurityService securityService) {
        this.uriBuilder = uriBuilder;
        this.securityService = securityService;
    }

    @Override
    public URI uri(Object methodInvocation) {
        return uriBuilder.build(methodInvocation);
    }

    @Override
    public LinksBuilder links() {
        return new DefaultLinksBuilder();
    }

    @Override
    public boolean isProjectFunctionGranted(int projectId, Class<? extends ProjectFunction> fn) {
        return securityService.isProjectFunctionGranted(projectId, fn);
    }

    @Override
    public boolean isGlobalFunctionGranted(Class<? extends GlobalFunction> fn) {
        return securityService.isGlobalFunctionGranted(fn);
    }

    @Override
    public boolean isLogged() {
        return securityService.isLogged();
    }

    protected class DefaultLinksBuilder implements LinksBuilder {

        private final Map<String, Link> links = new LinkedHashMap<>();

        @Override
        public LinksBuilder link(Link link) {
            links.put(link.getName(), link);
            return this;
        }

        @Override
        public LinksBuilder link(String name, URI uri) {
            return link(Link.of(name, uri));
        }

        @Override
        public LinksBuilder self(Object methodInvocation) {
            return link(Link.SELF, methodInvocation);
        }

        @Override
        public LinksBuilder link(String name, Object methodInvocation) {
            return link(name, uri(methodInvocation));
        }

        @Override
        public LinksBuilder link(String name, Object methodInvocation, boolean test) {
            if (test) {
                return link(name, methodInvocation);
            } else {
                return this;
            }
        }

        @Override
        public LinksBuilder link(String name, Object methodInvocation, Class<? extends GlobalFunction> fn) {
            return link(name, methodInvocation, securityService.isGlobalFunctionGranted(fn));
        }

        @Override
        public LinksBuilder link(String name, Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId) {
            return link(name, methodInvocation, securityService.isProjectFunctionGranted(projectId, fn));
        }

        @Override
        public LinksBuilder update(Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId) {
            return link(Link.UPDATE, methodInvocation, fn, projectId);
        }

        @Override
        public LinksBuilder delete(Object methodInvocation, Class<? extends ProjectFunction> fn, int projectId) {
            return link(Link.DELETE, methodInvocation, fn, projectId);
        }

        @Override
        public LinksBuilder page(String name, String path, Object... arguments) {
            return link(name, uriBuilder.page(path, arguments));
        }

        @Override
        public LinksBuilder page(String name, boolean allowed, String path, Object... arguments) {
            if (allowed) {
                return page(name, path, arguments);
            } else {
                return this;
            }
        }

        @Override
        public LinksBuilder page(String name, Class<? extends GlobalFunction> fn, String path, Object... arguments) {
            return page(name, securityService.isGlobalFunctionGranted(fn), path, arguments);
        }

        @Override
        public LinksBuilder page(String name, Class<? extends ProjectFunction> fn, ProjectEntity projectEntity, String path, Object... arguments) {
            return page(name, securityService.isProjectFunctionGranted(projectEntity, fn), path, arguments);
        }

        @Override
        public LinksBuilder entityPage(String name, boolean allowed, ProjectEntity projectEntity) {
            if (allowed) {
                return link(name, uriBuilder.getEntityPage(projectEntity));
            } else {
                return this;
            }
        }

        @Override
        public List<Link> build() {
            return new ArrayList<>(links.values());
        }
    }
}
