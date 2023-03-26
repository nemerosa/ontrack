package net.nemerosa.ontrack.extension.tfc.config

import net.nemerosa.ontrack.model.support.ConfigurationService

interface TFCConfigurationService : ConfigurationService<TFCConfiguration> {

    /**
     * Getting a configuration using a URL
     */
    fun findConfigurationByURL(url: String): TFCConfiguration?

}
