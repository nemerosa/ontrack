package net.nemerosa.ontrack.kdsl.spec.extension.av.trail

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.AutoVersioningAuditEntryByIdQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningAuditEntry
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.toAutoVersioningAuditEntry

class AutoVersioningBranchTrail(
    connector: Connector,
    val branch: Branch,
    val configuration: AutoVersioningSourceConfig,
    val rejectionReason: String?,
    val orderId: String?,
) : Connected(connector) {

    val audit: AutoVersioningAuditEntry? by lazy {
        orderId?.let { auditOrderId ->
            graphqlConnector.query(
                AutoVersioningAuditEntryByIdQuery(auditOrderId)
            )?.autoVersioningAuditEntries()?.pageItems()?.firstOrNull()
                ?.fragments()?.autoVersioningAuditEntryFragment()
                ?.toAutoVersioningAuditEntry()
        }
    }

}
