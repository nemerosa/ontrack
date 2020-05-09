package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryNotFoundException
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryIndicatorCategories(
        private val indicatorCategory: GQLTypeIndicatorCategory,
        private val indicatorCategoryService: IndicatorCategoryService
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("indicatorCategories")
                    .description("List of indicator categories")
                    .type(stdList(indicatorCategory.typeRef))
                    .argument {
                        it.name(ARG_ID)
                                .description("ID of the indicator category")
                                .type(GraphQLString)
                    }
                    .dataFetcher { env ->
                        val id: String? = env.getArgument(ARG_ID)
                        if (id != null) {
                            val category = indicatorCategoryService.getCategory(id)
                            listOf(category)
                        } else {
                            indicatorCategoryService.findAll()
                        }
                    }
                    .build()

    companion object {
        private const val ARG_ID = "id"
    }

}