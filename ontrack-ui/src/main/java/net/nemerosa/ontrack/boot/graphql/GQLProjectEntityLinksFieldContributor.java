package net.nemerosa.ontrack.boot.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

@Component
public class GQLProjectEntityLinksFieldContributor implements GQLProjectEntityFieldContributor {

    private final URIBuilder uriBuilder;
    private final SecurityService securityService;
    private final List<ResourceDecorator<?>> decorators;

    @Autowired
    public GQLProjectEntityLinksFieldContributor(
            URIBuilder uriBuilder,
            SecurityService securityService,
            List<ResourceDecorator<?>> decorators
    ) {
        this.uriBuilder = uriBuilder;
        this.securityService = securityService;
        this.decorators = decorators;
    }

    @Override
    public List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType) {
        List<GraphQLFieldDefinition> definitions = new ArrayList<>();
        // Links
        List<String> linkNames = decorators.stream()
                .filter(decorator -> decorator.appliesFor(projectEntityClass))
                .flatMap(decorator -> decorator.getLinkNames().stream())
                .distinct()
                .collect(Collectors.toList());
        if (linkNames != null && !linkNames.isEmpty()) {
            definitions.add(
                    newFieldDefinition()
                            .name("links")
                            .description("Links")
                            .type(
                                    newObject()
                                            .name(projectEntityClass.getSimpleName() + "Links")
                                            .description(projectEntityClass.getSimpleName() + " links")
                                            .fields(
                                                    linkNames.stream()
                                                            .map(linkName -> newFieldDefinition()
                                                                    .name(linkName)
                                                                    .type(GraphQLString)
                                                                    .build()
                                                            )
                                                            .collect(Collectors.toList())
                                            )
                                            .build()
                            )
                            .dataFetcher(projectEntityLinksFetcher(projectEntityClass))
                            .build()
            );
        }
        // OK
        return definitions;
    }

    private DataFetcher projectEntityLinksFetcher(Class<? extends ProjectEntity> projectEntityClass) {
        return environment -> {
            Object source = environment.getSource();
            if (projectEntityClass.isInstance(source)) {
                for (ResourceDecorator<?> decorator : decorators) {
                    if (decorator.appliesFor(projectEntityClass)) {
                        return getLinks(decorator, source);
                    }
                }
                return Collections.emptyMap();
            } else {
                return Collections.emptyMap();
            }
        };
    }

    private <T extends ProjectEntity> Map<String, String> getLinks(ResourceDecorator<?> decorator, Object source) {
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

    private ResourceContext createResourceContext() {
        return new DefaultResourceContext(
                uriBuilder,
                securityService
        );
    }

}
