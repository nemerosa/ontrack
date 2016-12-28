package net.nemerosa.ontrack.boot.graphql.schema;

import com.google.common.collect.ImmutableMap;
import graphql.schema.*;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
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
    private final ProjectEntityType projectEntityType;
    private final List<ResourceDecorator<?>> decorators;
    private final PropertyService propertyService;
    private final GQLTypeProperty property;

    public AbstractGQLProjectEntity(
            URIBuilder uriBuilder,
            SecurityService securityService,
            Class<T> projectEntityClass,
            ProjectEntityType projectEntityType, List<ResourceDecorator<?>> decorators, PropertyService propertyService, GQLTypeProperty property) {
        super(uriBuilder, securityService);
        this.projectEntityClass = projectEntityClass;
        this.projectEntityType = projectEntityType;
        this.decorators = decorators;
        this.propertyService = propertyService;
        this.property = property;
    }

    protected GraphQLInterfaceType projectEntityInterface() {
        return GraphQLInterfaceType.newInterface()
                .name(PROJECT_ENTITY)
                // Common fields
                .fields(baseProjectEntityInterfaceFields())
                // TODO Type resolver not set, but it should
                .typeResolver(new TypeResolverProxy())
                // OK
                .build();
    }

    protected List<GraphQLFieldDefinition> projectEntityInterfaceFields() {
        List<GraphQLFieldDefinition> definitions = baseProjectEntityInterfaceFields();
        // Properties
        definitions.add(
                newFieldDefinition()
                        .name("properties")
                        .description("List of properties")
                        // FIXME Arguments for the list of properties
                        .type(projectEntityPropertiesType())
                        .build()
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
                            .dataFetcher(projectEntityLinksFetcher())
                            .build()
            );
        }
        // OK
        return definitions;
    }

    private GraphQLOutputType projectEntityPropertiesType() {
        return newObject()
                .name(projectEntityClass.getSimpleName() + "Properties")
                .description("List of properties for " + projectEntityClass.getSimpleName())
                .fields(projectEntityProperties())
                .build();
    }

    private List<GraphQLFieldDefinition> projectEntityProperties() {
        return propertyService.getPropertyTypes().stream()
                // Gets properties which supports this type of entity
                .filter(propertyType -> propertyType.getSupportedEntityTypes().contains(projectEntityType))
                // Gets as a field definition
                .map(propertyType -> newFieldDefinition()
                        .name(propertyFieldName(propertyType))
                        .description(propertyType.getDescription())
                        .type(property.getType())
                        .build())
                // OK
                .collect(Collectors.toList());
    }

    private String propertyFieldName(PropertyType<?> propertyType) {
        return GraphqlUtils.lowerCamelCase(propertyType.getName());
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
