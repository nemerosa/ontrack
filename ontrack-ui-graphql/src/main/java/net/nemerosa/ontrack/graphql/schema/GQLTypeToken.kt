package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.structure.Token
import org.springframework.stereotype.Component

/**
 * Type for a [token][Token].
 */
@Component
class GQLTypeToken : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Authentication token")
            .dateField("creation", "Token creation date.")
            .dateField("validUntil", "Date until the end of validity.")
            .dateField("lastUsed", "Date when the token was last used.")
            .booleanField("valid", "Validity flag, computed in regard to current time.")
            .stringField("name", "Name of the token")
            .stringField("value", "Value of the token")
            .field {
                it.name("transient")
                    .description("Is this token managed?")
                    .type(GraphQLBoolean.toNotNull())
                    .dataFetcher { env ->
                        val token: Token = env.getSource()!!
                        token.scope.transient
                    }
            }
            .enumField(Token::scope)
            .build()

    override fun getTypeName(): String = Token::class.java.simpleName
}