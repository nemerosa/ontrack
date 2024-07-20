package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import org.springframework.stereotype.Component

@Component
class AutoVersioningTargetGQLPromotionLevelFieldContributor(
    private val gqlTypeAutoVersioningConfiguredBranch: GQLTypeAutoVersioningConfiguredBranch,
    private val autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
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
                    autoVersioningPromotionListenerService.getConfiguredBranches(pl)
                }
                .build(),
        )
    } else {
        null
    }

}