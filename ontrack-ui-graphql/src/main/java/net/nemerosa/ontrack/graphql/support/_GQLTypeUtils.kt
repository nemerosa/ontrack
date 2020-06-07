package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KProperty1

typealias TypeBuilder = GraphQLObjectType.Builder

fun TypeBuilder.idField(): GraphQLObjectType.Builder =
        fields(listOf(GraphqlUtils.idField()))

fun TypeBuilder.booleanField(name: String, description: String): GraphQLObjectType.Builder =
        field { it.name(name).description(description).type(GraphQLBoolean) }

fun TypeBuilder.intField(name: String, description: String): GraphQLObjectType.Builder =
        field { it.name(name).description(description).type(GraphQLInt) }

fun TypeBuilder.intField(property: KProperty<Int>, description: String): GraphQLObjectType.Builder =
        field { it.name(property.name).description(description).type(GraphQLInt) }

fun TypeBuilder.stringField(name: String, description: String): GraphQLObjectType.Builder =
        field {
            it.name(name).description(description).type(GraphQLString)
        }

fun TypeBuilder.dateField(name: String, description: String): GraphQLObjectType.Builder =

fun TypeBuilder.booleanField(property: KProperty<Boolean>, description: String? = null): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(GraphQLBoolean)
        }

fun <T> TypeBuilder.field(property: KProperty<T?>, type: GQLType, description: String? = null): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(type.typeRef)
        }

fun <E : Enum<E>> TypeBuilder.enumAsStringField(property: KProperty<E?>, description: String? = null): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(GraphQLString)
        }

fun <R, T> TypeBuilder.intField(property: KProperty1<R, T?>, description: String? = null, converter: (T) -> Int): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(GraphQLInt)
                    .dataFetcher { env ->
                        val t = property.get(env.getSource<R>())
                        t?.let { converter(it) }
                    }
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
