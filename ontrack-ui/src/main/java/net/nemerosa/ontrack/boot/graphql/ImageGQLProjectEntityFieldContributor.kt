package net.nemerosa.ontrack.boot.graphql

import graphql.Scalars
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.boot.ui.PromotionLevelController
import net.nemerosa.ontrack.boot.ui.ValidationStampController
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.net.URI

@Component
class ImageGQLProjectEntityFieldContributor(
    private val uriBuilder: URIBuilder
) : GQLProjectEntityFieldContributor {


    override fun getFields(
        projectEntityClass: Class<out ProjectEntity>,
        projectEntityType: ProjectEntityType
    ): List<GraphQLFieldDefinition> =
        when (projectEntityType) {
            ProjectEntityType.PROMOTION_LEVEL -> listOf(
                baseImageFieldBuilder { env ->
                    val pl: PromotionLevel = env.getSource()
                    uriBuilder.build(on(PromotionLevelController::class.java).getPromotionLevelImage_(null, pl.id))
                }
            )
            ProjectEntityType.VALIDATION_STAMP -> listOf(
                baseImageFieldBuilder { env ->
                    val vs: ValidationStamp = env.getSource()
                    uriBuilder.build(on(ValidationStampController::class.java).getValidationStampImage_(null, vs.id))
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