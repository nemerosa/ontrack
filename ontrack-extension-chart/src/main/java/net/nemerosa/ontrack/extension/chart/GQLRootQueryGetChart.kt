package net.nemerosa.ontrack.extension.chart

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGetChart(
    private val gqlInputGetChartInput: GQLInputGetChartInput,
    private val chartService: ChartService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("getChart")
        .description("Getting data for a chart")
        .argument {
            it.name(ARG_INPUT)
                .description("Parameters for getting the chart")
                .type(GraphQLNonNull(gqlInputGetChartInput.typeRef))
        }
        .type(GQLScalarJSON.INSTANCE)
        .dataFetcher { env ->
            val input: Any = env.getArgument<Any>(ARG_INPUT)
            val getChartInput = gqlInputGetChartInput.convert(input) ?: error("getChart input is required")
            chartService.getChart(getChartInput)
        }
        .build()

    companion object {
        const val ARG_INPUT = "input"
    }

}