package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.checkArgList
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.apache.commons.lang3.StringUtils.isNotBlank
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class GQLRootQueryBranches(
        private val structureService: StructureService,
        private val branch: GQLTypeBranch,
        private val propertyFilter: GQLInputPropertyFilter
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
                .argument(propertyFilter.asArgument())
                .dataFetcher(branchFetcher())
                .build()
    }

    private fun branchFetcher(): DataFetcher<*> {
        return DataFetcher<List<Branch>> { environment ->
            val id: Int? = environment.getArgument("id")
            val projectName: String? = environment.getArgument("project")
            val name: String? = environment.getArgument("name")
            val propertyFilterArg: GQLInputPropertyFilter? = environment.getArgument(GQLInputPropertyFilter.ARGUMENT_NAME)
            // Per ID
            if (id != null) {
                checkArgList(environment, "id")
                listOf(structureService.getBranch(ID.of(id)))
            } else if (isNotBlank(projectName) || isNotBlank(name) || propertyFilterArg != null) {

                // Project filter
                var projectFilter: (Project) -> Boolean = { true }
                if (isNotBlank(projectName)) {
                    projectFilter = projectFilter.and { p -> projectName == p.name }
                }

                // Branch filter
                var branchFilter: (Branch) -> Boolean = { true }
                if (isNotBlank(name)) {
                    val pattern = Pattern.compile(name)
                    branchFilter = branchFilter.and { b -> pattern.matcher(b.name).matches() }
                }

                // Property filter?
                if (propertyFilterArg != null) {
                    val filterObject: PropertyFilter? = propertyFilter.convert(propertyFilterArg)
                    val type = filterObject?.type
                    if (filterObject != null && type != null && type.isNotBlank()) {
                        branchFilter = branchFilter.and { b -> propertyFilter.getFilter(filterObject).test(b) }
                    }
                }

                // Gets the list of authorised projects
                structureService.projectList
                        // Filter on the project
                        .filter(projectFilter)
                        // Gets the list of branches
                        .flatMap { project -> structureService.getBranchesForProject(project.id) }
                        // Filter on the branch
                        .filter(branchFilter)
            } else {
                emptyList()
            }
        }
    }

}
