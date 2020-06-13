package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

/**
 * Representation of an error.
 */
@Component
class GQLTypeUserError: GQLType {

    override fun getTypeName(): String = USER_ERROR

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Representation of an error.")
                    .stringField(UserError::message)
                    .stringField(UserError::exception)
                    .stringField(UserError::location)
                    .build()

    companion object {
        /**
         * Type name
         */
        val USER_ERROR: String = UserError::class.java.simpleName
    }

}

data class UserError(
        @APIDescription("The error message")
        val message: String,
        @APIDescription("Programmatic code to be used by client. Usually the FQCN of the corresponding exception.")
        val exception: String,
        @APIDescription("Additional information about the location of this error.")
        val location: String? = null
)
