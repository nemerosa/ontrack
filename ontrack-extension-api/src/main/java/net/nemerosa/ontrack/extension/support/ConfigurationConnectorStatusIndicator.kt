package net.nemerosa.ontrack.extension.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.support.*

/**
 * Computing connector statuses based on [configurations][UserPasswordConfiguration].
 */
abstract class ConfigurationConnectorStatusIndicator<T : UserPasswordConfiguration<*>>(
        private val configurationService: ConfigurationService<T>,
        private val securityService: SecurityService
) : ConnectorStatusIndicator {

    override val statuses: List<ConnectorStatus>
        get() = securityService.callAsAdmin {
            configurationService.configurations.map { getConnectorStatus(it) }
        }

    private fun getConnectorStatus(config: T) = try {
        connect(config)
        ConnectorStatus.ok(connectorDescription(config))
    } catch (ex: Exception) {
        ConnectorStatus.error(connectorDescription(config), ex)
    }

    protected abstract fun connect(config: T)

    protected abstract fun connectorDescription(config: T): ConnectorDescription
}
