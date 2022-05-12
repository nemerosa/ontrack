package net.nemerosa.ontrack.extension.chart

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLInputGetChartInput : GQLInputType<GetChartInput> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(GetChartInput::class, dictionary)

    override fun convert(argument: Any?): GetChartInput? = argument?.asJson()?.parse()

    override fun getTypeRef() = GraphQLTypeReference(GetChartInput::class.java.simpleName)
}