package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parse

class AutoVersioningMgt(connector: Connector) : Connected(connector) {

    val stats: AutoVersioningStats
        get() = connector.get("/extension/auto-versioning/stats").body.parse()

    val audit: AutoVersioningAuditMgt = AutoVersioningAuditMgt(connector)

}