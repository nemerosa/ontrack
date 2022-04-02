package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeWebhook : GQLType {

    override fun getTypeName(): String = Webhook::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(Webhook::class))
            .stringField(Webhook::name)
            .booleanField(Webhook::enabled)
            .stringField(Webhook::url)
            .field {
                it.name("timeoutSeconds")
                    .description(getPropertyDescription(Webhook::timeout))
                    .type(GraphQLInt)
                    .dataFetcher { env ->
                        val webhook: Webhook = env.getSource()
                        webhook.timeout.toSeconds()
                    }
            }
            .build()
}