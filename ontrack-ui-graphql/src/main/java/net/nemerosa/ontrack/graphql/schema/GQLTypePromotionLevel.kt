package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component

@Component
class GQLTypePromotionLevel(
        private val structureService: StructureService,
        creation: GQLTypeCreation,
        private val promotionRun: GQLTypePromotionRun,
        projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
        private val projectEntityInterface: GQLProjectEntityInterface,
        freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>
) : AbstractGQLProjectEntity<PromotionLevel>(
        PromotionLevel::class.java,
        ProjectEntityType.PROMOTION_LEVEL,
        projectEntityFieldContributors,
        creation,
        freeTextAnnotatorContributors
) {

    override fun getTypeName(): String = PROMOTION_LEVEL

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(PROMOTION_LEVEL)
            .withInterface(projectEntityInterface.typeRef)
            .fields(projectEntityInterfaceFields())
            // Image flag
            .field {
                it.name("image")
                        .description("Flag to indicate if an image is associated")
                        .type(Scalars.GraphQLBoolean)
            }
            // Ref to branch
            .field {
                it.name("branch")
                        .description("Reference to branch")
                        .type(GraphQLTypeReference(GQLTypeBranch.BRANCH))
            }
            // Promotion runs
            .field {
                it.name("promotionRuns")
                        .description("List of runs for this promotion")
                        .type(GraphqlUtils.stdList(promotionRun.typeRef))
                        .argument(GraphqlUtils.stdListArguments())
                        .dataFetcher(promotionLevelPromotionRunsFetcher())
            }
            // OK
            .build()

    private fun promotionLevelPromotionRunsFetcher(): DataFetcher<List<PromotionRun>> =
            DataFetcher { environment: DataFetchingEnvironment ->
                val promotionLevel = environment.getSource<PromotionLevel>()
                // Gets all the promotion runs
                val promotionRuns = structureService.getPromotionRunsForPromotionLevel(promotionLevel.id)
                // Filters according to the arguments
                GraphqlUtils.stdListArgumentsFilter(promotionRuns, environment)
            }

    override fun getSignature(entity: PromotionLevel): Signature? {
        return entity.signature
    }

    companion object {
        const val PROMOTION_LEVEL = "PromotionLevel"
    }

}