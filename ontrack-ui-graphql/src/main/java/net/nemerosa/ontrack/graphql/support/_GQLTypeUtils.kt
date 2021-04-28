package net.nemerosa.ontrack.graphql.support

import graphql.Scalars
import graphql.Scalars.*
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLOutputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation

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

fun TypeBuilder.dateField(name: String, description: String): GraphQLObjectType.Builder = field {
    it.name(name)
        .description(description)
        .type(GQLScalarLocalDateTime.INSTANCE)
}

fun TypeBuilder.booleanField(property: KProperty<Boolean>, description: String? = null): GraphQLObjectType.Builder =
    field {
        it.name(property.name)
            .description(getPropertyDescription(property, description))
            .type(GraphQLBoolean)
    }

fun <T> TypeBuilder.field(property: KProperty<T?>, type: GQLType, description: String? = null): GraphQLObjectType.Builder =
        field {
            val outputType: GraphQLOutputType = if (property.returnType.isMarkedNullable) {
                type.typeRef
            } else {
                GraphQLNonNull(type.typeRef)
            }
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(outputType)
        }

fun <T> TypeBuilder.field(property: KProperty<T?>, typeName: String, description: String? = null): GraphQLObjectType.Builder =
    field(property, GraphQLTypeReference(typeName), description)

fun <T> TypeBuilder.field(property: KProperty<T?>, ref: GraphQLTypeReference, description: String? = null): GraphQLObjectType.Builder =
        field {
            val outputType: GraphQLOutputType = if (property.returnType.isMarkedNullable) {
                ref
            } else {
                GraphQLNonNull(ref)
            }
            it.name(property.name)
                    .description(getDescription(property, description))
                    .type(outputType)
        }

fun <E : Enum<E>> TypeBuilder.enumAsStringField(
    property: KProperty<E?>,
    description: String? = null,
): GraphQLObjectType.Builder =
    field {
        it.name(property.name)
            .description(getPropertyDescription(property, description))
            .type(GraphQLString)
    }

fun <R, T> TypeBuilder.intField(
    property: KProperty1<R, T?>,
    description: String? = null,
    converter: (T) -> Int,
): GraphQLObjectType.Builder =
    field {
        it.name(property.name)
            .description(getPropertyDescription(property, description))
            .type(GraphQLInt)
            .dataFetcher { env ->
                val t = property.get(env.getSource<R>())
                t?.let { converter(it) }
            }
    }

fun TypeBuilder.stringField(property: KProperty<String?>, description: String? = null): GraphQLObjectType.Builder =
        field {
            it.name(property.name)
                .description(getDescription(property, description))
                .type(nullableOutputType(GraphQLString, property.returnType.isMarkedNullable))
        }

fun TypeBuilder.creationField(name: String, description: String): GraphQLObjectType.Builder =
    field {
        it.name(name).description(description).type(GraphQLString)
    }

fun TypeBuilder.gqlTypeField(
    name: String,
    description: String,
    type: GQLType,
    nullable: Boolean = true,
): GraphQLObjectType.Builder =
    field {
        it.name(name).description(description).type(nullableType(type.typeRef, nullable))
    }

fun getDescription(type: KClass<*>, description: String? = null) =
    description
        ?: type.findAnnotation<APIDescription>()?.value
        ?: type.java.simpleName

fun getDescription(property: KProperty<*>, defaultDescription: String? = null): String? =
        defaultDescription
                ?: property.findAnnotation<APIDescription>()?.value
                ?: "${property.name} property"

fun nullableOutputType(type: GraphQLOutputType, nullable: Boolean) =
    if (nullable) {
        type
    } else {
        GraphQLNonNull(type)
    }
