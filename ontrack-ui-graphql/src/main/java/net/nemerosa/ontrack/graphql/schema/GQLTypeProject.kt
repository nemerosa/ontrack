package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.*
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.schema.actions.UIActionsGraphQLService
import net.nemerosa.ontrack.graphql.schema.actions.actions
import net.nemerosa.ontrack.graphql.support.disabledField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class GQLTypeProject(
        private val uiActionsGraphQLService: UIActionsGraphQLService,
        private val structureService: StructureService,
        private val projectLabelManagementService: ProjectLabelManagementService,
        creation: GQLTypeCreation,
        private val branch: GQLTypeBranch,
        private val validationRun: GQLTypeValidationRun,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val projectEntityInterface: GQLProjectEntityInterface,
        private val label: GQLTypeLabel,
        private val branchFavouriteService: BranchFavouriteService,
        private val projectFavouriteService: ProjectFavouriteService,
        private val branchModelMatcherService: BranchModelMatcherService,
        private val paginatedListFactory: GQLPaginatedListFactory,
        private val validationRunSearchService: ValidationRunSearchService,
        freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
) : AbstractGQLProjectEntity<Project>(
        Project::class.java,
        ProjectEntityType.PROJECT,
        projectEntityFieldContributors,
        creation,
        freeTextAnnotatorContributors
) {

    override fun getTypeName(): String {
        return PROJECT
    }

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return newObject()
                .name(PROJECT)
                .withInterface(projectEntityInterface.typeRef)
                .fields(projectEntityInterfaceFields())
                .field(disabledField())
                // Actions
                .actions(uiActionsGraphQLService, Project::class)
                // Is this project a favourite?
                .field {
                    it.name("favourite")
                            .description("Is this project a favourite of the current user?")
                            .type(GraphQLBoolean)
                            .dataFetcher { env ->
                                val project: Project = env.getSource()
                                projectFavouriteService.isProjectFavourite(project)
                            }
                }
                // Branches
                .field(
                        newFieldDefinition()
                                .name("branches")
                                .type(listType(branch.typeRef))
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
                                .argument {
                                    it.name(GRAPHQL_PROJECT_BRANCHES_USE_MODEL_ARG)
                                            .description("If set to true, filter on branch matching the project's branching model")
                                            .type(GraphQLBoolean)
                                }
                                .argument {
                                    it.name(ARG_BRANCHES_COUNT)
                                        .description("Maximum number of branches to return. No limit if not specified.")
                                        .type(GraphQLInt)
                                }
                                .argument {
                                    it.name(ARG_BRANCHES_ORDER)
                                        .description("If set to true, the branches will be ordered from the most recent build activity.")
                                        .type(GraphQLBoolean)
                                }
                                .dataFetcher(projectBranchesFetcher())
                                .build()
                )
                // Labels for this project
                .field {
                    it.name("labels")
                            .description("Labels for this project")
                            .type(listType(label.typeRef))
                            .dataFetcher { environment ->
                                val project: Project = environment.getSource()
                                projectLabelManagementService.getLabelsForProject(project)
                            }
                }
                // Search on validation runs
                .field(
                        paginatedListFactory.createPaginatedField(
                                cache = cache,
                                fieldName = "validationRuns",
                                fieldDescription = "Searching for validation runs in the project",
                                itemType = validationRun.typeName,
                                itemPaginatedListProvider = { environment, project: Project, offset, size ->
                                    findValidationsRuns(environment, project, offset, size)
                                },
                                arguments = listOf(
                                        newArgument()
                                                .name("branch")
                                                .description("Branch where to look for validation runs (regular expression, defaults to all)")
                                                .type(GraphQLString)
                                                .build(),
                                        newArgument()
                                                .name("validationStamp")
                                                .description("Validation stamp where to look for validation runs (regular expression, defaults to all)")
                                                .type(GraphQLString)
                                                .build(),
                                        newArgument()
                                                .name("statuses")
                                                .description("Validation status to look for (regular expression, defaults to all)")
                                                .type(GraphQLString)
                                                .build()
                                )
                        )
                )
                // OK
                .build()

    }

    private fun findValidationsRuns(
            environment: DataFetchingEnvironment,
            project: Project,
            offset: Int,
            size: Int
    ): PaginatedList<ValidationRun> {
        val request = ValidationRunSearchRequest(
                branch = environment.getArgument("branch"),
                validationStamp = environment.getArgument("validationStamp"),
                statuses = environment.getArgument("statuses"),
                offset = offset,
                size = size
        )
        return validationRunSearchService.searchProjectValidationRuns(project, request)
    }

    private fun projectBranchesFetcher(): DataFetcher<List<Branch>> {
        return DataFetcher { environment ->
            val source = environment.getSource<Any>()
            if (source is Project) {
                val name: String? = environment.getArgument<String>("name")
                val favorite: Boolean? = environment.getArgument(GRAPHQL_BRANCHES_FAVORITE_ARG)
                val useModel: Boolean? = environment.getArgument(GRAPHQL_PROJECT_BRANCHES_USE_MODEL_ARG)
                val count: Int? = environment.getArgument(ARG_BRANCHES_COUNT)
                val order: Boolean? = environment.getArgument(ARG_BRANCHES_ORDER)
                // Using a filter
                val filter = BranchFilter(
                    name = name,
                    favorite = favorite,
                    count = count,
                    order = order ?: false,
                )
                // Filtered list
                val branches = structureService.filterBranchesForProject(source, filter)
                // Final matching on the model
                val finalList = if (useModel != null && useModel) {
                    val branchModelMatcher = branchModelMatcherService.getBranchModelMatcher(source)
                    if (branchModelMatcher != null) {
                        branches.filter { branchModelMatcher.matches(it) }
                    } else {
                        branches
                    }
                } else {
                    branches
                }
                // Result
                finalList
            } else {
                emptyList()
            }
        }
    }

    override fun getSignature(entity: Project): Signature? {
        return entity.signature
    }

    companion object {
        const val PROJECT = "Project"
        const val GRAPHQL_PROJECT_BRANCHES_USE_MODEL_ARG = "useModel"
        /**
         * Maximum number of branches to return
         */
        const val ARG_BRANCHES_COUNT = "count"
        /**
         * Ordering the branches by decreasing build activity
         */
        const val ARG_BRANCHES_ORDER = "order"
    }

}
