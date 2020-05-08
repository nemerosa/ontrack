package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorType
import net.nemerosa.ontrack.extension.indicators.model.IndicatorTypeService
import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.idField
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
            .idField()
            .stringField("name", "Long name for the type")
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
            .build()

    override fun getTypeName(): String = ProjectIndicatorType::class.java.simpleName
}