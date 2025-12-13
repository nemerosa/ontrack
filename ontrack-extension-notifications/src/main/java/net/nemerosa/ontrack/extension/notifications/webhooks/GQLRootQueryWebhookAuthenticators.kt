package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryWebhookAuthenticators(
    private val gqlTypeWebhookAuthenticator: GQLTypeWebhookAuthenticator,
    private val webhookAuthenticatorRegistry: WebhookAuthenticatorRegistry,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("webhookAuthenticators")
            .description("List of available webhook authenticators")
            .type(listType(gqlTypeWebhookAuthenticator.typeRef))
            .dataFetcher { env ->
                webhookAuthenticatorRegistry.authenticators
            }
            .build()

}