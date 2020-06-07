package net.nemerosa.ontrack.graphql.support

import graphql.schema.*
import kotlin.reflect.KClass

inline fun <reified T : Any> objectField(
        name: String,
        description: String? = null,
        nullable: Boolean = true
) = objectField(T::class, name, description, nullable)

fun <T : Any> objectField(
        type: KClass<T>,
        name: String,
        description: String? = null,
        nullable: Boolean = true
) = objectField(
        type = type.toTypeRef(),
        name = name,
        description = description,
        nullable = nullable
)

fun objectField(
        type: GraphQLOutputType,
        name: String,
        description: String? = null,
        nullable: Boolean = true
): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name(name)
        .description(description)
        .type(nullableType(type, nullable))
        .build()

/**
 * Adjust a type so that it becomes nullable or not according to the value
 * of [nullable].
 */
fun nullableType(type: GraphQLOutputType, nullable: Boolean): GraphQLOutputType =
        if (nullable) {
            type
        } else {
            GraphQLNonNull(type)
        }
