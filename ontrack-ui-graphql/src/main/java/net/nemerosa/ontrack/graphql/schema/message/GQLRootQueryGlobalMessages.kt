package net.nemerosa.ontrack.graphql.schema.message

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.message.GlobalMessageService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGlobalMessages(
    private val gqlTypeMessage: GQLTypeMessage,
    private val globalMessageService: GlobalMessageService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("globalMessages")
            .description("List of global messages")
            .type(listType(gqlTypeMessage.typeRef))
            .dataFetcher {
                globalMessageService.globalMessages
            }
            .build()
}