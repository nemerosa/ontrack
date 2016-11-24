package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.TypeResolverProxy;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

// TODO Type providers?

@Component
@Deprecated
public class GQLModel {

    public static final String PROJECT_ENTITY = "ProjectEntity";
    public static final String BRANCH = "Branch";

    @Autowired
    private StructureService structureService;

    @Autowired
    private BuildFilterService buildFilterService;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Autowired
    private List<ResourceDecorator<?>> decorators;

    @Autowired
    private URIBuilder uriBuilder;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private GQLTypeBuild build;

    /**
     * Creates a context for the evaluation of links
     */
    private ResourceContext graphqlResourceContext() {
        return new DefaultResourceContext(
                uriBuilder,
                securityService
        );
    }

    private <T extends ProjectEntity> List<GraphQLFieldDefinition> projectEntityInterfaceFields(Class<T> projectEntityClass) {
        List<GraphQLFieldDefinition> definitions = new ArrayList<>(
                Arrays.asList(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField()
                )
        );
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

    private <T extends ProjectEntity> DataFetcher projectEntityLinksFetcher(Class<T> projectEntityClass) {
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
                graphqlResourceContext()
        ).stream().collect(Collectors.toMap(
                Link::getName,
                link -> link.getHref().toString()
        ));
    }

    private GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                .fields(projectEntityInterfaceFields(Project.class))
                // TODO Properties
                // TODO Type resolver not set, but it should
                .typeResolver(new TypeResolverProxy())
                // OK
                .build();
    }

    // TODO Define a ProjectEntity interface

}
