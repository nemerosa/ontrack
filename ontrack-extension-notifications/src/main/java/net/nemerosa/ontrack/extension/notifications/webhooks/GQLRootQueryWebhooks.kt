package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWebhooks(
    private val gqlTypeWebhook: GQLTypeWebhook,
    private val webhookAdminService: WebhookAdminService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("webhooks")
            .description("List of registred webhooks")
            .type(listType(gqlTypeWebhook.typeRef))
            .dataFetcher {
                webhookAdminService.webhooks
            }
            .build()
}