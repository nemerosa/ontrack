package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategoryService
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLFieldContributor
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.graphQLFieldContributions
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

/**
 * List of indicator types
 */
@Component
class GQLTypeIndicatorTypes(
        private val indicatorType: GQLTypeProjectIndicatorType,
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorCategoryService: IndicatorCategoryService,
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Aggregator of indicator types")
                    .field {
                        it.name("types")
                                .description("List of indicator types")
                                .type(listType(indicatorType.typeRef))
                                .argument { a ->
                                    a.name(ARG_ID)
                                            .description("ID of the indicator type")
                                            .type(Scalars.GraphQLString)
                                }
                                .argument { a ->
                                    a.name(ARG_CATEGORY)
                                            .description("ID of the indicator category")
                                            .type(Scalars.GraphQLString)
                                }
                                .dataFetcher { env ->
                                    val id: String? = env.getArgument(ARG_ID)
                                    val categoryId: String? = env.getArgument(ARG_CATEGORY)
                                    if (id != null) {
                                        val type = ProjectIndicatorType(indicatorTypeService.getTypeById(id))
                                        listOf(type)
                                    } else if (categoryId != null) {
                                        val category = indicatorCategoryService.getCategory(categoryId)
                                        indicatorTypeService.findByCategory(category).map { type ->
                                            ProjectIndicatorType(type)
                                        }
                                    } else {
                                        indicatorTypeService.findAll().map { type ->
                                            ProjectIndicatorType(type)
                                        }
                                    }
                                }
                    }
                    // Links
                    .fields(IndicatorTypes::class.java.graphQLFieldContributions(fieldContributors))
                    //OK
                    .build()

    override fun getTypeName(): String = "IndicatorTypes"

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_CATEGORY = "category"
    }

}

class IndicatorTypes
