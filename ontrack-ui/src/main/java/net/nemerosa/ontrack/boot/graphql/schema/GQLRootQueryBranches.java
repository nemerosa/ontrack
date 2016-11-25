package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;

import static graphql.Scalars.GraphQLInt;
import static graphql.Scalars.GraphQLString;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLFieldDefinition.newFieldDefinition;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.checkArgList;
import static net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils.stdList;

@Component
public class GQLRootQueryBranches implements GQLRootQuery {

    private final StructureService structureService;
    private final GQLTypeBranch branch;

    @Autowired
    public GQLRootQueryBranches(StructureService structureService, GQLTypeBranch branch) {
        this.structureService = structureService;
        this.branch = branch;
    }

    @Override
    public GraphQLFieldDefinition getFieldDefinition() {
        return newFieldDefinition()
                .name("branches")
                .type(stdList(branch.getType()))
                .argument(
                        newArgument()
                                .name("id")
                                .description("ID of the branch to look for")
                                .type(GraphQLInt)
                                .build()
                )
                .argument(
                        newArgument()
                                .name("project")
                                .description("Name of the project the branch belongs to")
                                .type(GraphQLString)
                                .build()
                )
                .dataFetcher(branchFetcher())
                .build();
    }

    private DataFetcher branchFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            String projectName = environment.getArgument("project");
            // Per ID
            if (id != null) {
                checkArgList(environment, "id");
                return Collections.singletonList(
                        structureService.getBranch(ID.of(id))
                );
            }
            // Per project name
            else if (StringUtils.isNotBlank(projectName)) {
                // Gets the project
                Optional<Project> oProject = structureService.findProjectByName(projectName);
                if (oProject.isPresent()) {
                    // TODO Might be a search by name as well, or by property
                    return structureService.getBranchesForProject(
                            oProject.get().getId()
                    );
                } else {
                    return Collections.emptyList();
                }
            }
            // Whole list
            else {
                return Collections.emptyList();
            }
        };
    }

}
