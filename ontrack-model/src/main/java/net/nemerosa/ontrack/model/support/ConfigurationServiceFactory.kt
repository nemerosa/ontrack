package net.nemerosa.ontrack.model.support

interface ConfigurationServiceFactory {

    /**
     * Given a configuration service type, returns the corresponding service.
     */
    fun findConfigurationService(type: String): ConfigurationService<*>?

}