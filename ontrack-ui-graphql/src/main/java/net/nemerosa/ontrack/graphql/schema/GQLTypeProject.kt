package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import java.util.*
import java.util.regex.Pattern

@Component
class GQLTypeProject(
        private val structureService: StructureService,
        private val projectLabelManagementService: ProjectLabelManagementService,
        creation: GQLTypeCreation,
        private val branch: GQLTypeBranch,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val projectEntityInterface: GQLProjectEntityInterface,
        private val label: GQLTypeLabel,
        private val branchFavouriteService: BranchFavouriteService
) : AbstractGQLProjectEntity<Project>(
        Project::class.java,
        ProjectEntityType.PROJECT,
        projectEntityFieldContributors,
        creation
) {

    override fun getTypeName(): String {
        return PROJECT
    }

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
                .name(PROJECT)
                .withInterface(projectEntityInterface.typeRef)
                .fields(projectEntityInterfaceFields())
                .field(GraphqlUtils.disabledField())
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(stdList(branch.typeRef))
                                .argument(
                                        newArgument()
                                                .name("name")
                                                .description("Regular expression to match against the branch name")
                                                .type(GraphQLString)
                                                .build()
                                )
                                .argument {
                                    it.name(GRAPHQL_BRANCHES_FAVORITE_ARG)
                                            .description("Gets only favorite branches")
                                            .type(GraphQLBoolean)
                                }
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // Labels for this project
                .field {
                    it.name("labels")
                            .description("Labels for this project")
                            .type(stdList(label.typeRef))
                            .dataFetcher { environment ->
                                val project: Project = environment.getSource()
                                projectLabelManagementService.getLabelsForProject(project)
                            }
                }
                // OK
                .build()

    }

    private fun projectBranchesFetcher(): DataFetcher<*> {
        return DataFetcher<List<Branch>> { environment ->
            val source = environment.getSource<Any>()
            if (source is Project) {
                val name: String? = environment.getArgument<String>("name")
                val favorite: Boolean? = environment.getArgument(GRAPHQL_BRANCHES_FAVORITE_ARG)
                // Combined filter
                var filter: (Branch) -> Boolean = { true }
                // Name criteria
                if (name != null) {
                    val nameFilter = Pattern.compile(name)
                    filter = filter.and { branch -> nameFilter.matcher(branch.name).matches() }
                }
                // Favourite
                if (favorite != null && favorite) {
                    filter = filter and { branchFavouriteService.isBranchFavourite(it) }
                }
                // Result
                structureService
                        .getBranchesForProject(source.id)
                        .filter(filter)
            } else {
                emptyList()
            }
        }
    }

    override fun getSignature(entity: Project): Optional<Signature> {
        return Optional.ofNullable(entity.signature)
    }

    companion object {
        const val PROJECT = "Project"
    }

}
