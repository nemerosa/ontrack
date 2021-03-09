package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

abstract class AbstractGQLEnum<E : Enum<E>>(
    type: KClass<E>,
    private val values: Array<E>,
    private val description: String
) : GQLEnum {

    private val typeName: String = type.java.simpleName

    override fun getTypeRef(): GraphQLTypeReference = GraphQLTypeReference(typeName)

    override fun createEnum(): GraphQLEnumType = GraphQLEnumType.newEnum()
        .name(typeName)
        .description(description)
        .apply {
            values.forEach { value ->
                value(value.name)
            }
        }
        .build()
}