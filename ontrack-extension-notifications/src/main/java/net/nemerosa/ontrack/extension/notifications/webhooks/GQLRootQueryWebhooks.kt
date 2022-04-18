package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.Scalars.GraphQLString
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
            .argument {
                it.name(ARG_NAME)
                    .description("Name of the webhook")
                    .type(GraphQLString)
            }
            .type(listType(gqlTypeWebhook.typeRef))
            .dataFetcher { env ->
                val name: String? = env.getArgument(ARG_NAME)
                if (name.isNullOrBlank()) {
                    webhookAdminService.webhooks
                } else {
                    listOfNotNull(
                        webhookAdminService.findWebhookByName(name)
                    )
                }
            }
            .build()

    companion object {
        private const val ARG_NAME = "name"
    }
}