package net.nemerosa.ontrack.graphql.schema

import graphql.schema.*
import org.springframework.stereotype.Component

/**
 * Interface implemented by all mutation payloads
 */
@Component
class GQLInterfacePayload : GQLInterface {

    companion object {
        /**
         * Interface name
         */
        const val PAYLOAD = "Payload"

        /**
         * `errors` field definition
         */
        fun payloadErrorsField(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
            .name("errors")
            .description("List of errors")
            .type(GraphQLList(GraphQLTypeReference(GQLTypeUserError.USER_ERROR)))
            .build()
    }

    override fun getTypeRef() = GraphQLTypeReference(PAYLOAD)

    override fun createInterface(): GraphQLInterfaceType = GraphQLInterfaceType.newInterface()
        .name(PAYLOAD)
        .description("Interface implemented by all mutation payloads")
        .field(payloadErrorsField())
        .typeResolver(TypeResolverProxy())
        .build()

}