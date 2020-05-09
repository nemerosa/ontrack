package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorTypes(
        private val indicatorType: GQLTypeProjectIndicatorType,
        private val indicatorTypeService: IndicatorTypeService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("indicatorTypes")
                    .description("List of indicator types")
                    .type(stdList(indicatorType.typeRef))
                    .argument {
                        it.name(ARG_ID)
                                .description("ID of the indicator type")
                                .type(GraphQLString)
                    }
                    .dataFetcher { env ->
                        val id: String? = env.getArgument(ARG_ID)
                        if (id != null) {
                            val type = ProjectIndicatorType(indicatorTypeService.getTypeById(id))
                            listOf(type)
                        } else {
                            indicatorTypeService.findAll().map {
                                ProjectIndicatorType(it)
                            }
                        }
                    }
                    .build()

    companion object {
        private const val ARG_ID = "id"
    }

}