package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BitbucketCloudConfigurationResourceDecorator(
    private val securityService: SecurityService
) : AbstractResourceDecorator<BitbucketCloudConfiguration>(
    BitbucketCloudConfiguration::class.java
) {

    /**
     * Obfuscates the password
     */
    override fun decorateBeforeSerialization(bean: BitbucketCloudConfiguration): BitbucketCloudConfiguration =
        bean.obfuscate()

    override fun links(configuration: BitbucketCloudConfiguration, resourceContext: ResourceContext): List<Link> {
        val globalSettingsGranted: Boolean = securityService.isGlobalFunctionGranted(GlobalSettings::class.java)
        return resourceContext.links()
            .self(on(BitbucketCloudConfigurationController::class.java).getConfiguration(configuration.name))
            .link(
                Link.DELETE,
                on(BitbucketCloudConfigurationController::class.java).deleteConfiguration(configuration.name),
                globalSettingsGranted
            )
            // OK
            .build()
    }

}