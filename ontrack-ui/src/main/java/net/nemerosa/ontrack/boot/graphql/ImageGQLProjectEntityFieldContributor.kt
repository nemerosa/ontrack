package net.nemerosa.ontrack.boot.graphql

import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.boot.ui.PromotionLevelController
import net.nemerosa.ontrack.boot.ui.ValidationStampController
import net.nemerosa.ontrack.graphql.schema.GQLProjectEntityFieldContributor
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ImageGQLProjectEntityFieldContributor
@Autowired
constructor(
        private val uriBuilder: URIBuilder
) : GQLProjectEntityFieldContributor {


    override fun getFields(projectEntityClass: Class<out ProjectEntity>, projectEntityType: ProjectEntityType): List<GraphQLFieldDefinition> {
        if (projectEntityType == ProjectEntityType.PROMOTION_LEVEL) {
            return listOf(
                    baseImageFieldBuilder().dataFetcher(GraphqlUtils.fetcher(
                            PromotionLevel::class.java,
                            { pl ->
                                uriBuilder.build(on(PromotionLevelController::class.java).getPromotionLevelImage_(
                                        null,
                                        pl.id
                                ))
                            }
                    )).build()
            )
        } else if (projectEntityType == ProjectEntityType.VALIDATION_STAMP) {
            return listOf(
                    baseImageFieldBuilder().dataFetcher(GraphqlUtils.fetcher(
                            ValidationStamp::class.java,
                            { vs ->
                                uriBuilder.build(on(ValidationStampController::class.java).getValidationStampImage_(
                                        null,
                                        vs.id
                                ))
                            }
                    )).build()
            )
        } else {
            return listOf()
        }
    }

    private fun baseImageFieldBuilder(): GraphQLFieldDefinition.Builder = GraphQLFieldDefinition.newFieldDefinition()
            .name("_image")
            .description("Link to the image")
            .type(Scalars.GraphQLString)
}