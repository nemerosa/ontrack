package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.extension.config.license.ConfigurationLicense
import org.springframework.stereotype.Service

@Service
class CoreConfigurationServiceImpl(
    private val configurationLicense: ConfigurationLicense,
) : CoreConfigurationService {

    fun configureBuild() {
        configurationLicense.checkConfigurationFeatureEnabled()
    }

}