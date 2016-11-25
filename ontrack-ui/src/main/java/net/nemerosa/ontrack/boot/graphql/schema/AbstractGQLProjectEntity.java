package net.nemerosa.ontrack.boot.graphql.schema;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.TypeResolverProxy;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.Signature;
import net.nemerosa.ontrack.ui.controller.URIBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public abstract class AbstractGQLProjectEntity<T extends ProjectEntity> extends AbstractGQLType {

    public static final String PROJECT_ENTITY = "ProjectEntity";

    private final Class<T> projectEntityClass;
    private final List<ResourceDecorator<?>> decorators;

    public AbstractGQLProjectEntity(
            URIBuilder uriBuilder,
            SecurityService securityService,
            Class<T> projectEntityClass,
            List<ResourceDecorator<?>> decorators) {
        super(uriBuilder, securityService);
        this.projectEntityClass = projectEntityClass;
        this.decorators = decorators;
    }

    protected GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                // Common fields
                .fields(baseProjectEntityInterfaceFields())
                // TODO Properties
                // TODO Type resolver not set, but it should
                .typeResolver(new TypeResolverProxy())
                // OK
                .build();
    }

    protected List<GraphQLFieldDefinition> projectEntityInterfaceFields() {
        List<GraphQLFieldDefinition> definitions = baseProjectEntityInterfaceFields();
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
                            .dataFetcher(projectEntityLinksFetcher())
                            .build()
            );
        }
        // OK
        return definitions;
    }

    private List<GraphQLFieldDefinition> baseProjectEntityInterfaceFields() {
        return new ArrayList<>(
                Arrays.asList(
                        GraphqlUtils.idField(),
                        GraphqlUtils.nameField(),
                        GraphqlUtils.descriptionField(),
                        newFieldDefinition()
                                .name("creation")
                                .type(
                                        newObject()
                                                .name("Signature")
                                                .field(
                                                        newFieldDefinition()
                                                                .name("user")
                                                                .description("User name")
                                                                .type(GraphQLString)
                                                                .build()
                                                )
                                                .field(
                                                        newFieldDefinition()
                                                                .name("time")
                                                                .description("ISO timestamp")
                                                                .type(GraphQLString)
                                                                .build()
                                                )
                                                .build()
                                )
                                .dataFetcher(creationFetcher())
                                .build()
                )
        );
    }

    protected DataFetcher creationFetcher() {
        return environment -> {
            Object source = environment.getSource();
            if (projectEntityClass.isInstance(source)) {
                @SuppressWarnings("unchecked")
                T entity = (T) source;
                return getSignature(entity)
                        .map(signature ->
                                ImmutableMap.of(
                                        "user", signature.getUser().getName(),
                                        "time", Time.forStorage(signature.getTime())
                                )
                        ).orElse(null);
            } else {
                return null;
            }
        };
    }

    protected abstract Optional<Signature> getSignature(T entity);


    private DataFetcher projectEntityLinksFetcher() {
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

}
