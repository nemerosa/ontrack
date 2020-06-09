package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.graphql.schema.authorizations.AuthorizationsService
import net.nemerosa.ontrack.graphql.schema.authorizations.authorizations
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class GQLTypeProject(
        private val authorizationsService: AuthorizationsService,
        private val structureService: StructureService,
        private val projectLabelManagementService: ProjectLabelManagementService,
        creation: GQLTypeCreation,
        private val branch: GQLTypeBranch,
        private val validationRun: GQLTypeValidationRun,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val projectEntityInterface: GQLProjectEntityInterface,
        private val label: GQLTypeLabel,
        private val branchFavouriteService: BranchFavouriteService,
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
                .field(GraphqlUtils.disabledField())
                // Authorizations
                .authorizations(authorizationsService, Project::class)
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
                                .argument {
                                    it.name(GRAPHQL_PROJECT_BRANCHES_USE_MODEL_ARG)
                                            .description("If set to true, filter on branch matching the project's branching model")
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
                // Search on validation runs
                .field(
                        paginatedListFactory.createPaginatedField(
                                cache = cache,
                                fieldName = "validationRuns",
                                fieldDescription = "Searching for validation runs in the project",
                                itemType = validationRun,
                                itemPaginatedListProvider = { environment, project: Project, offset, size ->
                                    findValidationsRuns(environment, project, offset, size)
                                },
                                arguments = listOf(
                                        GraphQLArgument.newArgument()
                                                .name("branch")
                                                .description("Branch where to look for validation runs (regular expression, defaults to all)")
                                                .type(GraphQLString)
                                                .build(),
                                        GraphQLArgument.newArgument()
                                                .name("validationStamp")
                                                .description("Validation stamp where to look for validation runs (regular expression, defaults to all)")
                                                .type(GraphQLString)
                                                .build(),
                                        GraphQLArgument.newArgument()
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
                // Matching the branching model
                if (useModel != null && useModel) {
                    val branchModelMatcher = branchModelMatcherService.getBranchModelMatcher(source)
                    if (branchModelMatcher != null) {
                        filter = filter and { branchModelMatcher.matches(it) }
                    }
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

    override fun getSignature(entity: Project): Signature? {
        return entity.signature
    }

    companion object {
        const val PROJECT = "Project"
        const val GRAPHQL_PROJECT_BRANCHES_USE_MODEL_ARG = "useModel"
    }

}
