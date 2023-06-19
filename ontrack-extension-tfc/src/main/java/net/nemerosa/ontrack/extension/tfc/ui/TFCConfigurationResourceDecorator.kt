package net.nemerosa.ontrack.extension.tfc.ui

import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class TFCConfigurationResourceDecorator(
    private val securityService: SecurityService
) : AbstractResourceDecorator<TFCConfiguration>(TFCConfiguration::class.java) {

    /**
     * Obfuscates the password
     */
    override fun decorateBeforeSerialization(bean: TFCConfiguration): TFCConfiguration = bean.obfuscate()

    override fun links(configuration: TFCConfiguration, resourceContext: ResourceContext): List<Link> {
        val globalSettingsGranted = securityService.isGlobalFunctionGranted(GlobalSettings::class.java)
        return resourceContext.links()
            .self(on(TFCController::class.java).getConfiguration(configuration.name))
            .link(
                Link.UPDATE,
                on(TFCController::class.java).updateConfigurationForm(configuration.name),
                globalSettingsGranted
            )
            .link(
                Link.DELETE,
                on(TFCController::class.java).deleteConfiguration(configuration.name),
                globalSettingsGranted
            )
            // OK
            .build()
    }
}
