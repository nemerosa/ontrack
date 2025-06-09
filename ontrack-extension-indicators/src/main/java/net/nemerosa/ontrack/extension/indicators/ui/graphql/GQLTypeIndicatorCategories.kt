package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

/**
 * List of indicator categories
 */
@Component
class GQLTypeIndicatorCategories(
        private val indicatorCategory: GQLTypeIndicatorCategory,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Aggregator of indicator categories")
                    .field {
                        it.name("categories")
                                .description("List of indicator categories")
                                .type(listType(indicatorCategory.typeRef))
                                .argument { a ->
                                    a.name(ARG_ID)
                                            .description("ID of the indicator category")
                                            .type(Scalars.GraphQLString)
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
                    }
                    // Links
                    .fields(IndicatorCategories::class.java.graphQLFieldContributions(fieldContributors))
                    //OK
                    .build()

    override fun getTypeName(): String = IndicatorCategories::class.java.simpleName

    companion object {
        private const val ARG_ID = "id"
    }

}

class IndicatorCategories
