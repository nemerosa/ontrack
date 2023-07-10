package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.disabledField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.model.buildfilter.BuildFilterProviderData
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component

@Component
class GQLTypeBranch(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
    creation: GQLTypeCreation,
    private val build: GQLTypeBuild,
    private val promotionLevel: GQLTypePromotionLevel,
    private val validationStamp: GQLTypeValidationStamp,
    private val inputBuildStandardFilter: GQLInputBuildStandardFilter,
    projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
    private val inputBuildGenericFilter: GQLInputBuildGenericFilter,
    private val projectEntityInterface: GQLProjectEntityInterface,
    freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val branchFavouriteService: BranchFavouriteService,
) : AbstractGQLProjectEntity<Branch>(
    Branch::class.java,
    ProjectEntityType.BRANCH,
    projectEntityFieldContributors,
    creation,
    freeTextAnnotatorContributors
) {

    override fun getTypeName(): String = BRANCH

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(BRANCH)
        .withInterface(projectEntityInterface.typeRef)
        .fields(projectEntityInterfaceFields())
        .field(disabledField())
        .field(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("project")
                .description("Reference to project")
                .type(GraphQLTypeReference(GQLTypeProject.PROJECT))
                .build()
        )
        // Is this branch a favourite?
        .field {
            it.name("favourite")
                .description("Is this branch a favourite of the current user?")
                .type(Scalars.GraphQLBoolean)
                .dataFetcher { env ->
                    val branch: Branch = env.getSource()
                    branchFavouriteService.isBranchFavourite(branch)
                }
        }
        // Promotion levels
        .field(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("promotionLevels")
                .type(listType(promotionLevel.typeRef))
                .dataFetcher(branchPromotionLevelsFetcher())
                .build()
        ) // Validation stamps
        .field(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("validationStamps")
                .type(listType(validationStamp.typeRef))
                .argument { arg: GraphQLArgument.Builder ->
                    arg.name("name")
                        .description("Filters on the validation stamp")
                        .type(Scalars.GraphQLString)
                }
                .dataFetcher(branchValidationStampsFetcher())
                .build()
        )
        // Builds for the branch (paginated)
        .field(
            gqlPaginatedListFactory.createPaginatedField<Branch, Build>(
                cache = cache,
                fieldName = "buildsPaginated",
                fieldDescription = "Gets a list of paginated builds for this branch.",
                itemType = build.typeName,
                arguments = listOf(
                    // Standard filter
                    GraphQLArgument.newArgument()
                        .name(ARG_FILTER)
                        .description("Filter based on build promotions, validations, properties, ...")
                        .type(inputBuildStandardFilter.typeRef)
                        .build(),
                    // Generic filter
                    GraphQLArgument.newArgument()
                        .name(ARG_GENERIC)
                        .description("Generic filter based on a configured filter")
                        .type(inputBuildGenericFilter.typeRef)
                        .build(),
                ),
                itemPaginatedListProvider = { environment, source, offset, size ->
                    val filter = environment.getArgument<Any>(ARG_FILTER)
                    val genericFilter = environment.getArgument<Any>(ARG_GENERIC)
                    val buildFilter: BuildFilterProviderData<*> = if (filter != null) {
                        inputBuildStandardFilter.convert(filter)
                    } else if (genericFilter != null) {
                        inputBuildGenericFilter.convert(genericFilter)
                    } else {
                        buildFilterService.standardFilterProviderData(size).build()
                    }
                    buildFilter.filterBranchBuildsWithPagination(source, offset, size)
                },
            )
        )
        // Builds for the branch (non paginated)
        .field(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("builds")
                .description("Gets a list of builds for this branch. Use `buildsPaginated` to get a list of paginated builds.")
                .type(listType(build.typeRef))
                .argument(
                    GraphQLArgument.newArgument()
                        .name("count")
                        .description("Maximum number of builds to return")
                        .type(Scalars.GraphQLInt)
                        .build()
                ) // Last promotion filter
                .argument(
                    GraphQLArgument.newArgument()
                        .name("lastPromotions")
                        .description("Filter which returns the last promoted builds")
                        .type(Scalars.GraphQLBoolean)
                        .build()
                ) // Standard filter
                .argument(
                    GraphQLArgument.newArgument()
                        .name(ARG_FILTER)
                        .description("Filter based on build promotions, validations, properties, ...")
                        .type(inputBuildStandardFilter.typeRef)
                        .build()
                ) // Generic filter
                .argument(
                    GraphQLArgument.newArgument()
                        .name(ARG_GENERIC)
                        .description("Generic filter based on a configured filter")
                        .type(inputBuildGenericFilter.typeRef)
                        .build()
                ) // Query
                .dataFetcher(branchBuildsFetcher())
                .build()
        ) // OK
        .build()

    private fun branchBuildsFetcher(): DataFetcher<*> {
        return DataFetcher { environment: DataFetchingEnvironment ->
            val source = environment.getSource<Any>()
            if (source is Branch) {
                // Count
                val count = environment.getArgument("count") ?: 10
                val filter = environment.getArgument<Any>(ARG_FILTER)
                val genericFilter = environment.getArgument<Any>(ARG_GENERIC)
                val lastPromotions = environment.getArgument<Boolean>("lastPromotions") ?: false
                // Filter to use
                // Last promotion filter
                val buildFilter: BuildFilterProviderData<*> = if (lastPromotions) {
                    buildFilterService.lastPromotedBuildsFilterData()
                } else if (filter != null) {
                    inputBuildStandardFilter.convert(filter)
                } else if (genericFilter != null) {
                    inputBuildGenericFilter.convert(genericFilter)
                } else {
                    buildFilterService.standardFilterProviderData(count).build()
                }
                // Result
                return@DataFetcher buildFilter.filterBranchBuilds(source)
            } else {
                return@DataFetcher emptyList<Any>()
            }
        }
    }

    private fun branchPromotionLevelsFetcher(): DataFetcher<*> {
        return DataFetcher { environment: DataFetchingEnvironment ->
            val source = environment.getSource<Any>()
            if (source is Branch) {
                val (id) = source
                return@DataFetcher structureService.getPromotionLevelListForBranch(id)
            } else {
                return@DataFetcher emptyList<Any>()
            }
        }
    }

    private fun branchValidationStampsFetcher(): DataFetcher<*> {
        return DataFetcher { environment: DataFetchingEnvironment ->
            val source = environment.getSource<Any>()
            val name: String? = environment.getArgument<String>("name")
            if (source is Branch) {
                val (id, name1, _, _, project) = source
                if (name != null) {
                    return@DataFetcher structureService.findValidationStampByName(
                        project.name, name1, name
                    )
                        .map { o: ValidationStamp -> listOf(o) }
                        .orElse(emptyList())
                } else {
                    return@DataFetcher structureService.getValidationStampListForBranch(id)
                }
            } else {
                return@DataFetcher emptyList<Any>()
            }
        }
    }

    override fun getSignature(entity: Branch): Signature? {
        return entity.signature
    }

    companion object {
        const val BRANCH = "Branch"

        const val ARG_FILTER = "filter"
        const val ARG_GENERIC = "generic"
    }
}