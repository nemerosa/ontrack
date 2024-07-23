package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component

@Component
class AutoVersioningTargetGQLPromotionLevelFieldContributor(
    private val gqlTypeAutoVersioningConfiguredBranch: GQLTypeAutoVersioningConfiguredBranch,
    private val autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
    private val autoVersioningTrackingService: AutoVersioningTrackingService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? = if (projectEntityType == ProjectEntityType.PROMOTION_LEVEL) {
        listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("autoVersioningTargets")
                .description("List of branches targeted for auto-versioning based on this promotion level")
                .type(listType(gqlTypeAutoVersioningConfiguredBranch.typeName))
                .dataFetcher { env ->
                    val pl: PromotionLevel = env.getSource()
                    val tracking = autoVersioningTrackingService.startInMemoryTrail()
                    autoVersioningPromotionListenerService.getConfiguredBranches(pl, tracking)
                    tracking.trail?.branches
                        ?.filter { it.isEligible() }
                        ?.map {
                            AutoVersioningConfiguredBranch(
                                branch = it.branch,
                                configuration = it.configuration,
                            )
                        } ?: emptyList<AutoVersioningConfiguredBranch>()
                }
                .build(),
        )
    } else {
        null
    }

}