package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.*
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLNonNull

/**
 * Input field as a int
 *
 * @param name Name of the field
 * @param description Description of the field
 * @param nullable Is the field nullable? (true by default)
 */
@Deprecated("Use intInputField instead")
fun inputInt(
    name: String,
    description: String?,
    nullable: Boolean = true,
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
    .name(name)
    .description(description)
    .type(nullableInputType(GraphQLInt, nullable))
    .build()

/**
 * Adjust a type so that it becomes nullable or not according to the value
 * of [nullable].
 */
fun nullableInputType(type: GraphQLInputType, nullable: Boolean): GraphQLInputType =
    if (nullable) {
        type
    } else {
        GraphQLNonNull(type)
    }
