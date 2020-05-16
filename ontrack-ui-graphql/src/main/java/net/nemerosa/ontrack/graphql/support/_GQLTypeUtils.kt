package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KProperty1

typealias TypeBuilder = graphql.schema.GraphQLObjectType.Builder

fun TypeBuilder.idField(): GraphQLObjectType.Builder =
        fields(listOf(GraphqlUtils.idField()))

fun TypeBuilder.booleanField(name: String, description: String): GraphQLObjectType.Builder =
        field { it.name(name).description(description).type(GraphQLBoolean) }

fun TypeBuilder.intField(name: String, description: String): GraphQLObjectType.Builder =
        field { it.name(name).description(description).type(GraphQLInt) }

fun TypeBuilder.stringField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }

fun TypeBuilder.dateField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }

// Extensions using @APIDescription when available

fun <T> TypeBuilder.booleanField(property: KProperty1<T, Boolean>): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property))
                    .type(GraphQLBoolean)
                    .dataFetcher { env ->
                        property.get(env.getSource())
                    }
        }

private fun getDescription(property: KProperty1<*, *>): String? =
        property.annotations
                .filterIsInstance<APIDescription>()
                .firstOrNull()
                ?.value
