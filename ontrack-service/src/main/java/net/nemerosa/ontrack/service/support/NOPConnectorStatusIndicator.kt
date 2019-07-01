package net.nemerosa.ontrack.service.support

import net.nemerosa.ontrack.model.support.ConnectorStatus
import net.nemerosa.ontrack.model.support.ConnectorStatusIndicator
import org.springframework.stereotype.Component

/**
 * Used only to make sure that indicator list is not empty.
 */
@Component
class NOPConnectorStatusIndicator : ConnectorStatusIndicator {

    override val type: String = "nop"

    override val statuses: List<ConnectorStatus> = emptyList()

}