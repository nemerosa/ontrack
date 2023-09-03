package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.authorizations.GQLInterfaceAuthorizableService
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.FreeTextAnnotatorContributor
import org.springframework.stereotype.Component

@Component
class GQLTypePromotionRun(
    creation: GQLTypeCreation,
    projectEntityFieldContributors: List<GQLProjectEntityFieldContributor>,
    private val projectEntityInterface: GQLProjectEntityInterface,
    freeTextAnnotatorContributors: List<FreeTextAnnotatorContributor>,
    private val gqlInterfaceAuthorizableService: GQLInterfaceAuthorizableService,
) : AbstractGQLProjectEntity<PromotionRun>(
    PromotionRun::class.java,
    ProjectEntityType.PROMOTION_RUN,
    projectEntityFieldContributors,
    creation,
    freeTextAnnotatorContributors
) {

    override fun getTypeName(): String = PROMOTION_RUN

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(PROMOTION_RUN)
            .withInterface(projectEntityInterface.getTypeRef())
            .fields(projectEntityInterfaceFields())
            // Authorizations
            .apply {
                gqlInterfaceAuthorizableService.apply(this, PromotionRun::class)
            }
            // Linked build
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("build")
                    .description("Associated build")
                    .type(GraphQLNonNull(GraphQLTypeReference(GQLTypeBuild.BUILD)))
                    .build()
            ) // Promotion level
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("promotionLevel")
                    .description("Associated promotion level")
                    .type(GraphQLNonNull(GraphQLTypeReference(GQLTypePromotionLevel.PROMOTION_LEVEL)))
                    .build()
            ) // OK
            .build()

    override fun getSignature(entity: PromotionRun): Signature? = entity.signature

    companion object {
        const val PROMOTION_RUN = "PromotionRun"
    }
}
