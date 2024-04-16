package net.nemerosa.ontrack.extension.support.client

import org.springframework.web.client.RestTemplate

/**
 * This service provides a `RestTemplate` for services to use.
 */
interface RestTemplateProvider {

    /**
     * Gets a REST template.
     */
    fun createRestTemplate(
        rootUri: String,
        basicAuthentication: RestTemplateBasicAuthentication
    ): RestTemplate

}