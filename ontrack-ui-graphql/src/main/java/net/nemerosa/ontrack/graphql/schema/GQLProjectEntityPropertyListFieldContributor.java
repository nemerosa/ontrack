package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLBoolean;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

@Component
public class GQLProjectEntityPropertyListFieldContributor implements GQLProjectEntityFieldContributor {

    private final PropertyService propertyService;
    private final GQLTypeProperty property;

    @Autowired
    public GQLProjectEntityPropertyListFieldContributor(PropertyService propertyService, GQLTypeProperty property) {
        this.propertyService = propertyService;
        this.property = property;
    }

    @Override
    public List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType) {
        return Collections.singletonList(
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
                        .dataFetcher(projectEntityPropertiesDataFetcher(projectEntityClass))
                        .build()
        );
    }

    private DataFetcher projectEntityPropertiesDataFetcher(Class<? extends ProjectEntity> projectEntityClass) {
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
