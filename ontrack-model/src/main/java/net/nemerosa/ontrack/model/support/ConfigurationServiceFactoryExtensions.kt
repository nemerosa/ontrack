package net.nemerosa.ontrack.model.support

fun ConfigurationServiceFactory.getConfigurationService(type: String) =
    findConfigurationService(type) ?: throw ConfigurationServiceTypeNotFoundException(type)
