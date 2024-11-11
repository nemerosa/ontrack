package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class GQLBuildPromotionInfoFieldContributor(
    private val buildPromotionInfoService: BuildPromotionInfoService,
) : GQLProjectEntityFieldContributor {
    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition>? =
        if (projectEntityType == ProjectEntityType.BUILD) {
            listOf(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("promotionInfo")
                    .description("Information about the promotions of this build")
                    .type(GraphQLTypeReference(BuildPromotionInfo::class.java.simpleName))
                    .dataFetcher { env ->
                        val build: Build = env.getSource()
                        buildPromotionInfoService.getBuildPromotionInfo(build)
                    }
                    .build()
            )
        } else {
            null
        }
}