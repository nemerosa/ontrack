package net.nemerosa.ontrack.extension.tfc.client

import net.nemerosa.ontrack.extension.tfc.config.TFCConfiguration

interface TFCClientFactory {

    /**
     * Given a configuration, returns an associated client
     */
    fun createClient(config: TFCConfiguration): TFCClient

}