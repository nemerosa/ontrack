package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConfigurationServiceFactory
import org.springframework.stereotype.Component

@Component
class ConfigurationServiceFactoryImpl(
    configurationServices: List<ConfigurationService<*>>,
) : ConfigurationServiceFactory {

    private val index = configurationServices.associateBy { it.type }

    override fun findConfigurationService(type: String): ConfigurationService<*>? = index[type]
}