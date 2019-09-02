package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class SonarQubeConfigurationResourceDecorator(
        private val securityService: SecurityService
) : AbstractResourceDecorator<SonarQubeConfiguration>(SonarQubeConfiguration::class.java) {

    /**
     * Obfuscates the password
     */
    override fun decorateBeforeSerialization(bean: SonarQubeConfiguration): SonarQubeConfiguration = bean.obfuscate()

    override fun links(configuration: SonarQubeConfiguration, resourceContext: ResourceContext): List<Link> {
        val globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings::class.java)
        return resourceContext.links()
                .self(on(SonarQubeController::class.java).getConfiguration(configuration.name))
                .link(Link.UPDATE, on(SonarQubeController::class.java).updateConfigurationForm(configuration.name), globalSettingsGranted)
                .link(Link.DELETE, on(SonarQubeController::class.java).deleteConfiguration(configuration.name), globalSettingsGranted)
                // OK
                .build()
    }
}
