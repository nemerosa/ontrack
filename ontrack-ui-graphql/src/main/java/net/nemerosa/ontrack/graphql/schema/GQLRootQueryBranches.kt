package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.checkArgList
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

const val GRAPHQL_BRANCHES_FAVORITE_ARG = "favourite"

@Component
class GQLRootQueryBranches(
        private val structureService: StructureService,
        private val branch: GQLTypeBranch,
        private val propertyFilter: GQLInputPropertyFilter,
        private val branchFavouriteService: BranchFavouriteService
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition {
        return newFieldDefinition()
                .name("branches")
                .type(stdList(branch.typeRef))
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
                .argument(
                        newArgument()
                                .name("name")
                                .description("Regular expression to match against the branch name")
                                .type(GraphQLString)
                                .build()
                )
                .argument {
                    it.name(GRAPHQL_BRANCHES_FAVORITE_ARG)
                            .description("Keeps only branches listed as favourute")
                            .type(GraphQLBoolean)
                }
                .argument(propertyFilter.asArgument())
                .dataFetcher(branchFetcher())
                .build()
    }

    private fun branchFetcher(): DataFetcher<List<Branch>> = DataFetcher { environment ->
        val id: Int? = environment.getArgument("id")
        val projectName: String? = environment.getArgument("project")
        val name: String? = environment.getArgument("name")
        val favourite: Boolean? = environment.getArgument(GRAPHQL_BRANCHES_FAVORITE_ARG)
        val propertyFilterArg: Any? = environment.getArgument(GQLInputPropertyFilter.ARGUMENT_NAME)

        // Project filter
        val projectFilter: (Project) -> Boolean = if (projectName.isNullOrBlank()) {
            { true }
        } else {
            { project -> project.name == projectName }
        }

        // Branch name filter
        val regex = name?.toRegex()
        val branchNameFilter: (Branch) -> Boolean = if (regex == null) {
            { true }
        } else {
            { branch -> regex.matches(branch.name) }
        }

        // Property filter?
        val branchPropertyFilter: (Branch) -> Boolean = if (propertyFilterArg != null) {
            val filterObject: PropertyFilter? = propertyFilter.convert(propertyFilterArg)
            val type = filterObject?.type
            if (filterObject != null && type != null && type.isNotBlank()) {
                { b: Branch -> propertyFilter.getFilter(filterObject).test(b) }
            } else {
                { true }
            }
        } else {
            { true }
        }

        // Branch filter
        val branchFilter = branchNameFilter and branchPropertyFilter

        // Per ID
        if (id != null) {
            checkArgList(environment, "id")
            listOf(structureService.getBranch(ID.of(id)))
        } else if (favourite == true) {
            // Gets the list of favourite branches
            val branches = branchFavouriteService.getFavouriteBranches()
            // Filter them with project, name & property
            branches.filter {
                projectFilter(it.project) && branchFilter(it)
            }
        } else if (!projectName.isNullOrBlank()) {
            // Gets the project
            val project = structureService.findProjectByName(projectName).getOrNull()
            // Gets the branches for this project
            val branches = project?.let { structureService.getBranchesForProject(project.id) } ?: emptyList()
            // Filter them with name & property
            branches.filter(branchFilter)
        } else {
            // Gets the projects
            structureService.projectList.asSequence()
                    // Filter on the project
                    .filter(projectFilter)
                    // Gets the list of branches
                    .flatMap { project -> structureService.getBranchesForProject(project.id).asSequence() }
                    // Filter on the branch
                    .filter(branchFilter)
                    // OK
                    .toList()
        }
    }

}
