package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException;
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException;
import net.nemerosa.ontrack.model.structure.*;
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
public class GQLRootQueryBuilds implements GQLRootQuery {

    public static final String PROJECT_ARGUMENT = "project";
    public static final String BRANCH_ARGUMENT = "branch";
    private final StructureService structureService;
    private final BuildFilterService buildFilterService;
    private final GQLTypeBuild build;

    @Autowired
    public GQLRootQueryBuilds(StructureService structureService, BuildFilterService buildFilterService, GQLTypeBuild build) {
        this.structureService = structureService;
        this.buildFilterService = buildFilterService;
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
                .argument(
                        newArgument()
                                .name(PROJECT_ARGUMENT)
                                .description("Name of a project")
                                .type(GraphQLString)
                                .build()
                )
                .argument(
                        newArgument()
                                .name(BRANCH_ARGUMENT)
                                .description("Name of a branch - requires 'project' to be filled as well")
                                .type(GraphQLString)
                                .build()
                )
                .dataFetcher(buildFetcher())
                .build();
    }

    private DataFetcher buildFetcher() {
        return environment -> {
            Integer id = environment.getArgument("id");
            Optional<String> oProject = GraphqlUtils.getStringArgument(environment, PROJECT_ARGUMENT);
            Optional<String> oBranch = GraphqlUtils.getStringArgument(environment, BRANCH_ARGUMENT);
            // Per ID
            if (id != null) {
                checkArgList(environment, "id");
                return Collections.singletonList(
                        structureService.getBuild(ID.of(id))
                );
            }
            // Per project
            else if (oProject.isPresent()) {
                // ... and branch
                if (oBranch.isPresent()) {
                    // Gets the branch
                    Branch branch = structureService.findBranchByName(oProject.get(), oBranch.get())
                            .orElseThrow(() -> new BranchNotFoundException(oProject.get(), oBranch.get()));
                    // Gets the first builds
                    // TODO Configurable branch filter
                    BuildFilterProviderData<?> filter = buildFilterService.standardFilterProviderData(10).build();
                    // Runs the filter
                    return filter.filterBranchBuilds(branch);
                }
                // Project only
                else {
                    // Gets the project
                    Project project = structureService.findProjectByName(oProject.get())
                            .orElseThrow(() -> new ProjectNotFoundException(oProject.get()));
                    // TODO Build search form as argument
                    BuildSearchForm form = new BuildSearchForm().withMaximumCount(10);
                    return structureService.buildSearch(project.getId(), form);
                }
            }
            // None
            else {
                return Collections.emptyList();
            }
        };
    }

}
