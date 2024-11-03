package net.nemerosa.ontrack.graphql.schema

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

fun <T : Any> rootQuery(
    name: String,
    description: String,
    arguments: List<GraphQLArgument> = emptyList(),
    outputType: KClass<T>,
    code: (env: DataFetchingEnvironment) -> T?
): GraphQLFieldDefinition =
    GraphQLFieldDefinition.newFieldDefinition()
        .name(name)
        .description(description)
        .arguments(arguments)
        .type(GraphQLTypeReference(outputType.simpleName))
        .dataFetcher { env ->
            code(env)
        }
        .build()
