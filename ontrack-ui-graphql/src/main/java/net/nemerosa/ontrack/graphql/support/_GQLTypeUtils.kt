package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
import graphql.schema.GraphQLObjectType

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
