package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.structure.DecorationService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;

@Component
public class GQLProjectEntityDecorationListFieldContributor implements GQLProjectEntityFieldContributor {

    private final DecorationService decorationService;
    private final GQLTypeDecoration decoration;

    @Autowired
    public GQLProjectEntityDecorationListFieldContributor(DecorationService decorationService, GQLTypeDecoration decoration) {
        this.decorationService = decorationService;
        this.decoration = decoration;
    }

    @Override
    public List<GraphQLFieldDefinition> getFields(Class<? extends ProjectEntity> projectEntityClass, ProjectEntityType projectEntityType) {
        return Collections.singletonList(
                newFieldDefinition()
                        .name("decorations")
                        .description("List of decorations")
                        .argument(
                                newArgument()
                                        .name("type")
                                        .description("Fully qualified name of the decoration type")
                                        .type(GraphQLString)
                                        .build()
                        )
                        .type(GraphqlUtils.stdList(decoration.getTypeRef()))
                        .dataFetcher(projectEntityDecorationsDataFetcher(projectEntityClass))
                        .build()
        );
    }

    private DataFetcher projectEntityDecorationsDataFetcher(Class<? extends ProjectEntity> projectEntityClass) {
        return environment -> {
            Object o = environment.getSource();
            if (projectEntityClass.isInstance(o)) {
                // Filters
                Optional<String> typeFilter = GraphqlUtils.getStringArgument(environment, "type");
                // Gets the raw list
                return decorationService.getDecorations((ProjectEntity) o).stream()
                        // Filter by type
                        .filter(property -> typeFilter
                                .map(typeFilterName -> StringUtils.equals(
                                        typeFilterName,
                                        property.getDecorationType()
                                ))
                                .orElse(true)
                        )
                        // OK
                        .collect(Collectors.toList());
            } else {
                return null;
            }
        };
    }
}
