package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import kotlin.reflect.KProperty

typealias TypeBuilder = GraphQLObjectType.Builder

fun TypeBuilder.idField(): GraphQLObjectType.Builder =
        fields(listOf(GraphqlUtils.idField()))

fun TypeBuilder.intField(name: String, description: String): GraphQLObjectType.Builder =
        field { it.name(name).description(description).type(GraphQLInt) }

fun TypeBuilder.intField(property: KProperty<Int>, description: String): GraphQLObjectType.Builder =
        field { it.name(property.name).description(description).type(GraphQLInt) }

fun TypeBuilder.stringField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }

fun TypeBuilder.creationField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }
