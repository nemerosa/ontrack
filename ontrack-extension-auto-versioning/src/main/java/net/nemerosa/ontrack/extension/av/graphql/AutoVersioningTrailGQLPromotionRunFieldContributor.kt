package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrackingService
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component

@Component
class AutoVersioningTrailGQLPromotionRunFieldContributor(
    private val autoVersioningTrackingService: AutoVersioningTrackingService,
    private val gqlTypeAutoVersioningTrail: GQLTypeAutoVersioningTrail,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType,
    ): List<GraphQLFieldDefinition>? = if (projectEntityType == ProjectEntityType.PROMOTION_RUN) {
        listOf(
            GraphQLFieldDefinition.newFieldDefinition()
                .name("autoVersioningTrail")
                .description("List of branches targeted for auto-versioning based on this promotion run or with their reason for rejection")
                .type(gqlTypeAutoVersioningTrail.typeRef)
                .dataFetcher { env ->
                    val run: PromotionRun = env.getSource()!!
                    autoVersioningTrackingService.getTrail(run)
                }
                .build(),
        )
    } else {
        null
    }

}