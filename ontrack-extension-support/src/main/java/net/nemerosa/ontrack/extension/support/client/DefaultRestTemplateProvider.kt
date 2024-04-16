package net.nemerosa.ontrack.extension.support.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.support.client",
    name = ["resttemplate"],
    havingValue = "default",
    matchIfMissing = true,
)
open class DefaultRestTemplateProvider : RestTemplateProvider {

    override fun createRestTemplate(
        rootUri: String,
        basicAuthentication: RestTemplateBasicAuthentication
    ): RestTemplate =
        RestTemplateBuilder()
            .rootUri(rootUri)
            .basicAuthentication(basicAuthentication.username, basicAuthentication.password)
            .build()

}