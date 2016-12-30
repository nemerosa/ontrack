package net.nemerosa.ontrack.boot.graphql.schema;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInterfaceType;
import graphql.schema.TypeResolverProxy;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.Signature;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static graphql.schema.GraphQLObjectType.newObject;

public abstract class AbstractGQLProjectEntity<T extends ProjectEntity> implements GQLType {

    public static final String PROJECT_ENTITY = "ProjectEntity";

    private final Class<T> projectEntityClass;
    private final ProjectEntityType projectEntityType;
    private final PropertyService propertyService;
    private final GQLTypeProperty property;
    private final List<GQLProjectEntityFieldContributor> projectEntityFieldContributors;

    public AbstractGQLProjectEntity(
            Class<T> projectEntityClass,
            ProjectEntityType projectEntityType,
            PropertyService propertyService,
            GQLTypeProperty property,
            List<GQLProjectEntityFieldContributor> projectEntityFieldContributors) {
        this.projectEntityClass = projectEntityClass;
        this.projectEntityType = projectEntityType;
        this.propertyService = propertyService;
        this.property = property;
        this.projectEntityFieldContributors = projectEntityFieldContributors;
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
        // Properties list
        // TODO Extract as contributor
        definitions.add(
                newFieldDefinition()
                        .name("properties")
                        .description("List of properties")
                        .argument(
                                newArgument()
                                        .name("type")
                                        .description("Fully qualified name of the property type")
                                        .type(GraphQLString)
                                        .build()
                        )
                        .argument(
                                newArgument()
                                        .name("hasValue")
                                        .description("Keeps properties having a value")
                                        .type(GraphQLBoolean)
                                        .defaultValue(false)
                                        .build()
                        )
                        .type(GraphqlUtils.stdList(property.getType()))
                        .dataFetcher(projectEntityPropertiesDataFetcher())
                        .build()
        );
        // For all contributors
        definitions.addAll(
                projectEntityFieldContributors.stream()
                        .map(contributor -> contributor.getFields(projectEntityClass, projectEntityType))
                        .filter(Objects::nonNull)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList())
        );
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


    private DataFetcher projectEntityPropertiesDataFetcher() {
        return environment -> {
            Object o = environment.getSource();
            if (projectEntityClass.isInstance(o)) {
                // Filters
                Optional<String> typeFilter = GraphqlUtils.getStringArgument(environment, "type");
                boolean hasValue = GraphqlUtils.getBooleanArgument(environment, "hasValue", false);
                // Gets the raw list
                return propertyService.getProperties((ProjectEntity) o).stream()
                        // Filter by type
                        .filter(property -> typeFilter
                                .map(typeFilterName -> StringUtils.equals(
                                        typeFilterName,
                                        property.getTypeDescriptor().getTypeName()
                                ))
                                .orElse(true)
                        )
                        // Filter by value
                        .filter(property -> !hasValue || !property.isEmpty())
                        // OK
                        .collect(Collectors.toList());
            } else {
                return null;
            }
        };
    }

}
