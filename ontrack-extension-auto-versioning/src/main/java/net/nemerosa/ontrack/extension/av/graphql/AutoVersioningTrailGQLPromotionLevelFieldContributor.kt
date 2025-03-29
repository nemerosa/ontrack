package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component

@Component
class AutoVersioningTrailGQLPromotionLevelFieldContributor(
    private val autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
    private val autoVersioningTrackingService: AutoVersioningTrackingService,
    private val gqlTypeAutoVersioningTrail: GQLTypeAutoVersioningTrail,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? = if (projectEntityType == ProjectEntityType.PROMOTION_LEVEL) {
        listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("autoVersioningTrail")
                .description("List of branches targeted for auto-versioning based on this promotion level or with their reason for rejection")
                .type(gqlTypeAutoVersioningTrail.typeRef)
                .dataFetcher { env ->
                    val pl: PromotionLevel = env.getSource()!!
                    val tracking = autoVersioningTrackingService.startInMemoryTrail()
                    autoVersioningPromotionListenerService.getConfiguredBranches(pl, tracking)
                    tracking.trail
                }
                .build(),
        )
    } else {
        null
    }

}