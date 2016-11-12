package net.nemerosa.ontrack.boot.graphql.schema;

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
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryBranches implements GQLRootQuery {

    private final StructureService structureService;
    private final GQLModel model;

    @Autowired
    public GQLRootQueryBranches(StructureService structureService, GQLModel model) {
        this.structureService = structureService;
        this.model = model;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("branches")
                .type(stdList(model.branchType()))
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the branch to look for")
                                .type(new GraphQLNonNull(GraphQLInt))
                                .build()
                )
                .dataFetcher(branchFetcher())
                .build();
    }

    private DataFetcher branchFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            if (id != null) {
                return Collections.singletonList(
                        structureService.getBranch(ID.of(id))
                );
            }
            // Whole list
            else {
                return Collections.emptyList();
            }
        };
    }

}
