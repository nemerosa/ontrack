package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static graphql.Scalars.GraphQLInt;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.checkArgList;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryBuilds implements GQLRootQuery {

    private final StructureService structureService;
    private final GQLTypeBuild build;

    @Autowired
    public GQLRootQueryBuilds(StructureService structureService, GQLTypeBuild build) {
        this.structureService = structureService;
        this.build = build;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("builds")
                .type(stdList(build.getType()))
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the build to look for")
                                .type(GraphQLInt)
                                .build()
                )
                .dataFetcher(buildFetcher())
                .build();
    }

    private DataFetcher buildFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            // Per ID
            if (id != null) {
                checkArgList(environment, "id");
                return Collections.singletonList(
                        structureService.getBuild(ID.of(id))
                );
            }
            // None
            else {
                return Collections.emptyList();
            }
        };
    }

}
