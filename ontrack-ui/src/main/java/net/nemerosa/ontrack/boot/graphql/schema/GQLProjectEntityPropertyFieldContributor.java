package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.PropertyType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

@Component
public class GQLProjectEntityPropertyFieldContributor implements GQLProjectEntityFieldContributor {

    private final PropertyService propertyService;
    private final GQLTypeProperty property;

    @Autowired
    public GQLProjectEntityPropertyFieldContributor(PropertyService propertyService, GQLTypeProperty property) {
        this.propertyService = propertyService;
        this.property = property;
    }

    @Override
    public List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType) {
        return propertyService.getPropertyTypes().stream()
                // Gets properties which supports this type of entity
                .filter(propertyType -> propertyType.getSupportedEntityTypes().contains(projectEntityType))
                // Gets as a field definition
                .map(propertyType -> newFieldDefinition()
                        .name(propertyFieldName(propertyType))
                        .description(propertyType.getDescription())
                        .type(property.getType())
                        .dataFetcher(projectEntityPropertyDataFetcher(propertyType, projectEntityClass))
                        .build())
                // OK
                .collect(Collectors.toList());
    }

    private String propertyFieldName(PropertyType<?> propertyType) {
        String baseName = StringUtils.uncapitalize(propertyType.getClass().getSimpleName());
        return StringUtils.substringBeforeLast(baseName, "Type");
    }

    private <P> DataFetcher projectEntityPropertyDataFetcher(PropertyType<P> propertyType, Class<? extends ProjectEntity> projectEntityClass) {
        return environment -> {
            Object source = environment.getSource();
            if (projectEntityClass.isInstance(source)) {
                ProjectEntity projectEntity = (ProjectEntity) source;
                return propertyService.<P>getProperty(
                        projectEntity,
                        propertyType.getClass().getName()
                );
            } else {
                return null;
            }
        };
    }
}
