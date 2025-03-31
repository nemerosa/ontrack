package net.nemerosa.ontrack.boot.graphql

import graphql.Scalars
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.ui.controller.EntityURIBuilder
import org.springframework.stereotype.Component
import java.net.URI

@Component
class ImageGQLProjectEntityFieldContributor(
    private val uriBuilder: EntityURIBuilder
) : GQLProjectEntityFieldContributor {


    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> =
        when (projectEntityType) {
            ProjectEntityType.PROMOTION_LEVEL -> listOf(
                baseImageFieldBuilder { env ->
                    val pl: PromotionLevel = env.getSource()!!
                    uriBuilder.url("/rest/structure/promotionLevels/${pl.id}/image")
                }
            )
            ProjectEntityType.VALIDATION_STAMP -> listOf(
                baseImageFieldBuilder { env ->
                    val vs: ValidationStamp = env.getSource()!!
                    uriBuilder.url("/rest/structure/validationStamps/${vs.id}/image")
                }
            )
            else -> emptyList()
        }

    @Suppress("DEPRECATION")
    private fun baseImageFieldBuilder(
        dataFetcher: (env: DataFetchingEnvironment) -> URI
    ): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("_image")
        .description("Link to the image")
        .type(Scalars.GraphQLString)
        .dataFetcher(dataFetcher)
        .build()

}