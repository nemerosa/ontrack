package net.nemerosa.ontrack.graphql.support

import graphql.Scalars.GraphQLBoolean
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLNonNull

/**
 * Input field as a string
 *
 * @param name Name of the field
 * @param description Description of the field
 * @param nullable Is the field nullable? (true by default)
 */
fun inputString(
        name: String,
        description: String?,
        nullable: Boolean = true
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(name)
        .description(description)
        .type(nullableInputType(GraphQLString, nullable))
        .build()

/**
 * Input field as a boolean
 *
 * @param name Name of the field
 * @param description Description of the field
 * @param nullable Is the field nullable? (true by default)
 */
fun inputBoolean(
        name: String,
        description: String?,
        nullable: Boolean = true
): GraphQLInputObjectField = GraphQLInputObjectField.newInputObjectField()
        .name(name)
        .description(description)
        .type(nullableInputType(GraphQLBoolean, nullable))
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
