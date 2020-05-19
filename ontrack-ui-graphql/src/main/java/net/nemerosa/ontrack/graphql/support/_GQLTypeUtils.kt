package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

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

fun TypeBuilder.booleanField(property: KProperty<Boolean>, description: String = ""): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(GraphQLBoolean)
        }

fun TypeBuilder.stringField(property: KProperty<String?>, description: String): GraphQLObjectType.Builder =
        field { it.name(property.name).description(description).type(GraphQLString) }

fun TypeBuilder.creationField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }

fun getDescription(property: KProperty<*>, defaultDescription: String? = null): String? =
        defaultDescription
                ?: property.findAnnotation<APIDescription>()?.value
                ?: "${property.name} property"
