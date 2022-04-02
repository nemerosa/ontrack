package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import net.nemerosa.ontrack.ui.resource.DefaultResourceContext
import net.nemerosa.ontrack.ui.resource.ResourceModule
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapper
import net.nemerosa.ontrack.ui.resource.ResourceObjectMapperFactory
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class DefaultWebhookPayloadRenderer(
    uriBuilder: URIBuilder,
    securityService: SecurityService,
    resourceModules: List<ResourceModule>,
) : WebhookPayloadRenderer {

    private val resourceObjectMapper: ResourceObjectMapper

    init {
        val resourceContext = DefaultResourceContext(uriBuilder, securityService)
        val mapper = ObjectMapperFactory.create()
        resourceObjectMapper = ResourceObjectMapperFactory(mapper).resourceObjectMapper(
            resourceModules,
            resourceContext
        )
    }

    override fun render(payload: WebhookPayload<*>): ByteArray {
        val output = ByteArrayOutputStream()
        resourceObjectMapper.write(output, payload)
        return output.toByteArray()
    }
}