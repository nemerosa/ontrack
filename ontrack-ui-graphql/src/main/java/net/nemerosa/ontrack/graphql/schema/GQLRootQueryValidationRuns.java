package net.nemerosa.ontrack.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLNonNull;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryValidationRuns implements GQLRootQuery {

    private final StructureService structureService;
    private final GQLTypeValidationRun validationRun;

    @Autowired
    public GQLRootQueryValidationRuns(StructureService structureService, GQLTypeValidationRun validationRun) {
        this.structureService = structureService;
        this.validationRun = validationRun;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("validationRuns")
                .type(stdList(validationRun.getType()))
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the validation run to look for")
                                .type(new GraphQLNonNull(GraphQLInt))
                                .build()
                )
                .dataFetcher(validationRunFetcher())
                .build();
    }

    private DataFetcher validationRunFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                // Fetch by ID
                return Collections.singletonList(
                        structureService.getValidationRun(ID.of(id))
                );
            }
            // Empty list
            else {
                return Collections.emptyList();
            }
        };
    }

}
