package net.nemerosa.ontrack.extension.notifications.webhooks

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription
import java.net.http.HttpRequest

interface WebhookAuthenticator<C> {

    @APIDescription("Webhook authentication identifier")
    val type: String

    @APIDescription("Webhook authentication display name")
    val displayName: String

    fun validateConfig(node: JsonNode): C

    fun authenticate(config: C, builder: HttpRequest.Builder)

    fun obfuscate(config: C): C

    fun merge(input: C, existing: C): C

}