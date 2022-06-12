package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.AutoVersioningAuditEntriesQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Management interface for the audit of the auto versioning
 */
class AutoVersioningAuditMgt(connector: Connector) : Connected(connector) {


    /**
     * Gets the list of auto versioning audit entries
     */
    fun entries(
        offset: Int = 0,
        size: Int = 10,
        source: String? = null,
        project: String,
        branch: String? = null,
    ): List<AutoVersioningAuditEntry> =
        graphqlConnector.query(
            AutoVersioningAuditEntriesQuery.builder()
                .offset(offset)
                .size(size)
                .source(source)
                .project(project)
                .branch(branch)
                .build()
        )?.autoVersioningAuditEntries()?.pageItems()?.map { item ->
            AutoVersioningAuditEntry(
                order = AutoVersioningOrder(
                    uuid = item.order().uuid(),
                ),
                running = item.running() ?: false,
                mostRecentState = AutoVersioningAuditEntryState(
                    state = item.mostRecentState().state().name,
                    data = item.mostRecentState().data(),
                ),
                audit = item.audit().map { auditEntry ->
                    AutoVersioningAuditEntryState(
                        state = auditEntry.state().name,
                        data = auditEntry.data(),
                    )
                }
            )
        } ?: emptyList()

}