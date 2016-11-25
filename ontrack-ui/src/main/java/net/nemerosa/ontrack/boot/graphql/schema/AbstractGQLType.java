package net.nemerosa.ontrack.boot.graphql.schema;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractGQLType implements GQLType {

    private final URIBuilder uriBuilder;
    private final SecurityService securityService;

    @Autowired
    public AbstractGQLType(URIBuilder uriBuilder, SecurityService securityService) {
        this.uriBuilder = uriBuilder;
        this.securityService = securityService;
    }

    /**
     * Creates a context for the evaluation of links
     */
    protected ResourceContext createResourceContext() {
        return new DefaultResourceContext(
                uriBuilder,
                securityService
        );
    }

    protected <T extends ProjectEntity> Map<String, String> getLinks(ResourceDecorator<?> decorator, Object source) {
        @SuppressWarnings("unchecked")
        ResourceDecorator<T> resourceDecorator = (ResourceDecorator<T>) decorator;
        @SuppressWarnings("unchecked")
        T t = (T) source;

        return resourceDecorator.links(
                t,
                createResourceContext()
        ).stream().collect(Collectors.toMap(
                Link::getName,
                link -> link.getHref().toString()
        ));
    }

}
