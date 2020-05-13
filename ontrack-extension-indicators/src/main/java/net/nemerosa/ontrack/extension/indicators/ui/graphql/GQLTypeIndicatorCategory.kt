package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorCategory(
        private val indicatorType: GQLTypeProjectIndicatorType,
        private val indicatorSource: GQLTypeIndicatorSource,
        private val indicatorTypeService: IndicatorTypeService
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Indicator category")
            // Core fields
            .stringField("id", "Indicator category ID")
            .stringField("name", "Indicator category name")
            // Source
            .field {
                it.name(IndicatorCategory::source.name)
                        .description("Source for this category")
                        .type(indicatorSource.typeRef)
            }
            // List of types in this category
            .field {
                it.name("types")
                        .description("List of indicator types belonging to this category.")
                        .type(stdList(indicatorType.typeRef))
                        .dataFetcher { env ->
                            val category = env.getSource<IndicatorCategory>()
                            indicatorTypeService.findByCategory(category).map {
                                ProjectIndicatorType(it)
                            }
                        }
            }
            // OK
            .build()

    override fun getTypeName(): String = INDICATOR_CATEGORY

    companion object {
        val INDICATOR_CATEGORY = IndicatorCategory::class.java.simpleName
    }
}