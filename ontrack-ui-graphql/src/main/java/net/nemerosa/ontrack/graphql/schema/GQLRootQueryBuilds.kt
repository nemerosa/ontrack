package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.exceptions.ArgumentMismatchException
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryBuilds(
        private val structureService: StructureService,
        private val build: GQLTypeBuild,
        private val inputBuildStandardFilter: GQLInputBuildStandardFilter,
        private val inputBuildSearchForm: GQLInputBuildSearchForm
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("builds")
                .type(listType(build.typeRef, nullable = true))
                .argument(
                        GraphQLArgument.newArgument()
                                .name("id")
                                .description("ID of the build to look for")
                                .type(Scalars.GraphQLInt)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(PROJECT_ARGUMENT)
                                .description("Name of a project")
                                .type(Scalars.GraphQLString)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(BRANCH_ARGUMENT)
                                .description("Name of a branch - requires 'project' to be filled as well")
                                .type(Scalars.GraphQLString)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(NAME_ARGUMENT)
                                .description("Name of a build - requires 'project' & 'branch' to be filled as well")
                                .type(Scalars.GraphQLString)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(BUILD_BRANCH_FILTER_ARGUMENT)
                                .description("Filter to apply for the builds on the branch - requires 'branch' to be filled.")
                                .type(inputBuildStandardFilter.typeRef)
                                .build()
                )
                .argument(
                        GraphQLArgument.newArgument()
                                .name(BUILD_PROJECT_FILTER_ARGUMENT)
                                .description("Filter to apply for the builds on the project - requires 'project' to be filled.")
                                .type(inputBuildSearchForm.typeRef)
                                .build()
                )
                .dataFetcher(buildFetcher())
                .build()
    }

    private fun buildFetcher(): DataFetcher<List<Build>> {
        return DataFetcher { environment: DataFetchingEnvironment ->
            val id = environment.getArgument<Int>("id")
            val projectName: String? = environment.getArgument(PROJECT_ARGUMENT)
            val branchName: String? = environment.getArgument(BRANCH_ARGUMENT)
            val name: String? = environment.getArgument(NAME_ARGUMENT)
            val branchFilter = environment.getArgument<Any>(BUILD_BRANCH_FILTER_ARGUMENT)
            val projectFilter = environment.getArgument<Any>(BUILD_PROJECT_FILTER_ARGUMENT)
            // Per ID
            if (id != null) {
                listOf<Build>(
                        structureService.getBuild(of(id))
                )
            } else if (!projectName.isNullOrBlank()) {
                // ... and branch
                if (!branchName.isNullOrBlank()) {
                    // Name only
                    if (!name.isNullOrBlank()) {
                        listOfNotNull(
                                structureService.findBuildByName(projectName, branchName, name)
                                        .getOrNull()
                        )
                    } else {
                        // Gets the branch
                        val branch = structureService.findBranchByName(projectName, branchName)
                                .getOrNull()
                                ?: return@DataFetcher emptyList()
                        // Runs the filter
                        val filter = inputBuildStandardFilter.convert(branchFilter)
                        filter.filterBranchBuilds(branch)
                    }
                } else {
                    // Gets the project
                    val (id1) = structureService.findProjectByName(projectName)
                            .getOrNull()
                            ?: return@DataFetcher emptyList()
                    // Build search form as argument
                    val form = inputBuildSearchForm.convert(projectFilter)
                    return@DataFetcher structureService.buildSearch(id1, form)
                }
            } else if (branchFilter != null) {
                throw ArgumentMismatchException("$BUILD_BRANCH_FILTER_ARGUMENT must be used together with $BRANCH_ARGUMENT")
            } else if (projectFilter != null) {
                throw ArgumentMismatchException("$BUILD_PROJECT_FILTER_ARGUMENT must be used together with $PROJECT_ARGUMENT")
            } else {
                emptyList<Build>()
            }
        }
    }

    companion object {
        const val PROJECT_ARGUMENT = "project"
        const val BRANCH_ARGUMENT = "branch"
        const val NAME_ARGUMENT = "name"
        const val BUILD_BRANCH_FILTER_ARGUMENT = "buildBranchFilter"
        const val BUILD_PROJECT_FILTER_ARGUMENT = "buildProjectFilter"
    }
}
