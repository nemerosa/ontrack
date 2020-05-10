package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicatorType(
        private val indicatorTypeService: IndicatorTypeService,
        private val indicatorValueType: GQLTypeIndicatorValueType
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Type of indicator")
            .stringField("id", "Unique ID for the type")
            .stringField("shortName", "Short name for the indicator type")
            .stringField("name", "Long name for the indicator type")
            .stringField("link", "Link to the definition of the indicator")
            // Category
            .field {
                it.name(ProjectIndicatorType::category.name)
                        .description("Associated category")
                        .type(GraphQLTypeReference(GQLTypeIndicatorCategory.INDICATOR_CATEGORY))
            }
            // Value type
            .field {
                it.name(IndicatorType<*, *>::valueType.name)
                        .description("Value type")
                        .type(indicatorValueType.typeRef)
                        .dataFetcher { env ->
                            val typeRef = env.getSource<ProjectIndicatorType>()
                            val type = indicatorTypeService.getTypeById(typeRef.id)
                            type.valueType
                        }
            }
            // OK
            .build()

    override fun getTypeName(): String = ProjectIndicatorType::class.java.simpleName
}