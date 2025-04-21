package net.nemerosa.ontrack.extension.support.client

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
@ConditionalOnWebApplication
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