package net.nemerosa.ontrack.extension.indicators.ui.graphql

import graphql.Scalars.GraphQLInt
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import java.time.Duration

typealias DefinitionBuilder = GraphQLFieldDefinition.Builder

const val ARG_DURATION = "duration"

fun DefinitionBuilder.durationArgument(): DefinitionBuilder =
        argument {
            it.name(ARG_DURATION)
                    .description("Number of seconds (> 0) to get indicator trends from.")
                    .type(GraphQLInt)
        }

fun DataFetchingEnvironment.getDurationArgument() =
        getArgument<Int?>(ARG_DURATION)?.let { seconds ->
            Duration.ofSeconds(seconds.toLong())
        }
