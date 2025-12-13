package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWebhookByName(
    private val gqlTypeWebhook: GQLTypeWebhook,
    private val webhookAdminService: WebhookAdminService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("webhookByName")
            .description("Getting a webhook using its name")
            .argument(stringArgument(ARG_NAME, "Name of the webhook", nullable = false))
            .type(gqlTypeWebhook.typeRef)
            .dataFetcher { env ->
                val name: String = env.getArgument(ARG_NAME)!!
                webhookAdminService.findWebhookByName(name)
            }
            .build()

    companion object {
        const val ARG_NAME = "name"
    }
}