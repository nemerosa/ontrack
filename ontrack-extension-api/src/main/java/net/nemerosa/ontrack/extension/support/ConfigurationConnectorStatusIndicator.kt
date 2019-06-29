package net.nemerosa.ontrack.extension.support

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConnectorStatus
import net.nemerosa.ontrack.model.support.ConnectorStatusIndicator
import net.nemerosa.ontrack.model.support.UserPasswordConfiguration

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

    protected abstract fun getConnectorStatus(config: T): ConnectorStatus
}
