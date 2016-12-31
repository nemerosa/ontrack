package net.nemerosa.ontrack.boot.graphql.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import net.nemerosa.ontrack.boot.graphql.support.GraphqlUtils;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData;
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
    public static final String BUILD_BRANCH_FILTER_ARGUMENT = "buildBranchFilter";
    public static final String BUILD_PROJECT_FILTER_ARGUMENT = "buildProjectFilter";

    private final StructureService structureService;
    private final GQLTypeBuild build;
    private final GQLInputBuildStandardFilter inputBuildStandardFilter;
    private final GQLInputBuildSearchForm inputBuildSearchForm;

    @Autowired
    public GQLRootQueryBuilds(
            StructureService structureService,
            GQLTypeBuild build,
            GQLInputBuildStandardFilter inputBuildStandardFilter,
            GQLInputBuildSearchForm inputBuildSearchForm
    ) {
        this.structureService = structureService;
        this.build = build;
        this.inputBuildStandardFilter = inputBuildStandardFilter;
        this.inputBuildSearchForm = inputBuildSearchForm;
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
                .argument(
                        newArgument()
                                .name(BUILD_BRANCH_FILTER_ARGUMENT)
                                .description("Filter to apply for the builds on the branch - requires 'branch' to be filled.")
                                .type(inputBuildStandardFilter.getInputType())
                                .build()
                )
                .argument(
                        newArgument()
                                .name(BUILD_PROJECT_FILTER_ARGUMENT)
                                .description("Filter to apply for the builds on the project - requires 'project' to be filled.")
                                .type(inputBuildSearchForm.getInputType())
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
            Object branchFilter = environment.getArgument(BUILD_BRANCH_FILTER_ARGUMENT);
            Object projectFilter = environment.getArgument(BUILD_PROJECT_FILTER_ARGUMENT);
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
                    // Configurable branch filter
                    BuildFilterProviderData<?> filter = inputBuildStandardFilter.convert(branchFilter);
                    // Runs the filter
                    return filter.filterBranchBuilds(branch);
                }
                // Project only
                else {
                    // Gets the project
                    Project project = structureService.findProjectByName(oProject.get())
                            .orElseThrow(() -> new ProjectNotFoundException(oProject.get()));
                    // Build search form as argument
                    BuildSearchForm form = inputBuildSearchForm.convert(projectFilter);
                    return structureService.buildSearch(project.getId(), form);
                }
            }
            // Branch filter only - not accepted
            else if (branchFilter != null) {
                throw new IllegalStateException(String.format(
                        "%s must be used together with %s",
                        BUILD_BRANCH_FILTER_ARGUMENT,
                        BRANCH_ARGUMENT
                ));
            }
            // Project filter only - not accepted
            else if (projectFilter != null) {
                throw new IllegalStateException(String.format(
                        "%s must be used together with %s",
                        BUILD_PROJECT_FILTER_ARGUMENT,
                        PROJECT_ARGUMENT
                ));
            }
            // None
            else {
                return Collections.emptyList();
            }
        };
    }

}
