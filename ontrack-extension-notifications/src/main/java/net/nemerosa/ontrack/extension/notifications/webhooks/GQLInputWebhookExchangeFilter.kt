package net.nemerosa.ontrack.extension.notifications.webhooks

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLInputWebhookExchangeFilter : GQLInputType<WebhookExchangeFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(WebhookExchangeFilter::class, dictionary)

    override fun convert(argument: Any?): WebhookExchangeFilter? =
        argument?.asJson()?.parse() ?: WebhookExchangeFilter()

    override fun getTypeRef() = GraphQLTypeReference(WebhookExchangeFilter::class.java.simpleName)
}