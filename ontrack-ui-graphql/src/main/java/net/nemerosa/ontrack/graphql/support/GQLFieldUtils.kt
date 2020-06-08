package net.nemerosa.ontrack.graphql.support

import graphql.schema.*
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import kotlin.reflect.KClass

// ============================================================================
// Single object field
// ============================================================================

inline fun <reified T : Any> objectField(
        name: String,
        description: String? = null,
        nullable: Boolean = true,
        noinline fetcher: ((DataFetchingEnvironment) -> T?)? = null
) = objectField(T::class, name, description, nullable, fetcher)

fun <T : Any> objectField(
        type: KClass<T>,
        name: String,
        description: String? = null,
        nullable: Boolean = true,
        fetcher: ((DataFetchingEnvironment) -> T?)? = null
) = objectField(
        type = type.toTypeRef(),
        name = name,
        description = description,
        nullable = nullable,
        fetcher = fetcher
)

fun <T> objectField(
        type: GraphQLOutputType,
        name: String,
        description: String? = null,
        nullable: Boolean = true,
        fetcher: ((DataFetchingEnvironment) -> T?)? = null
): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name(name)
        .description(description)
        .type(nullableType(type, nullable))
        .apply {
            fetcher?.let { dataFetcher(it) }
        }
        .build()

// ============================================================================
// List of objects
// ============================================================================

inline fun <reified T : Any> listField(
        name: String,
        description: String,
        nullable: Boolean = false,
        noinline fetcher: (DataFetchingEnvironment) -> List<T>
) = listField(
        type = T::class,
        name = name,
        description = description,
        nullable = nullable,
        fetcher = fetcher
)

fun <T : Any> listField(
        type: KClass<T>,
        name: String,
        description: String,
        nullable: Boolean = false,
        fetcher: (DataFetchingEnvironment) -> List<T>
) = listField(
        type = type.toTypeRef(),
        name = name,
        description = description,
        nullable = nullable,
        fetcher = fetcher
)

fun <T> listField(
        type: GraphQLOutputType,
        name: String,
        description: String,
        nullable: Boolean = false,
        fetcher: (DataFetchingEnvironment) -> List<T>
): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name(name)
        .description(description)
        .type(listType(type, nullable))
        .dataFetcher(fetcher)
        .build()

// ============================================================================
// General utilities
// ============================================================================

/**
 * List type
 */
fun listType(itemType: GraphQLOutputType, nullable: Boolean = false, nullableItem: Boolean = false): GraphQLOutputType =
        nullableType(
                GraphQLList(nullableType(itemType, nullableItem)),
                nullable
        )

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
