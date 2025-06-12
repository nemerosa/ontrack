package net.nemerosa.ontrack.extension.support.client

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.web.client.RestTemplate

/**
 * This service provides a `RestTemplate` for services to use.
 */
interface RestTemplateProvider {

    /**
     * Gets a REST template.
     */
    @Deprecated("Will be removed in V5. Use the method with the configuration block.")
    fun createRestTemplate(
        rootUri: String,
        basicAuthentication: RestTemplateBasicAuthentication
    ): RestTemplate

    /**
     * Gets a REST template.
     */
    fun createRestTemplate(
        rootUri: String,
        configuration: RestTemplateBuilder.() -> RestTemplateBuilder,
    ): RestTemplate

}