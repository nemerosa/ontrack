package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.AutoVersioningAuditEntriesQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.AutoVersioningAuditEntryByIdQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

/**
 * Management interface for the audit of the auto versioning
 */
class AutoVersioningAuditMgt(connector: Connector) : Connected(connector) {

    /**
     * Gets an entry by ID
     */
    fun findEntryById(uuid: String): AutoVersioningAuditEntry? =
        graphqlConnector.query(
            AutoVersioningAuditEntryByIdQuery(uuid)
        )?.autoVersioningAuditEntries()?.pageItems()?.firstOrNull()
            ?.fragments()?.autoVersioningAuditEntryFragment()
            ?.toAutoVersioningAuditEntry()

    /**
     * Gets the list of auto versioning audit entries
     */
    fun entries(
        offset: Int = 0,
        size: Int = 10,
        source: String? = null,
        project: String,
        branch: String? = null,
        version: String? = null,
    ): List<AutoVersioningAuditEntry> =
        graphqlConnector.query(
            AutoVersioningAuditEntriesQuery.builder()
                .offset(offset)
                .size(size)
                .source(source)
                .project(project)
                .branch(branch)
                .version(version)
                .build()
        )?.autoVersioningAuditEntries()?.pageItems()?.map { item ->
            item.fragments().autoVersioningAuditEntryFragment().toAutoVersioningAuditEntry()
        } ?: emptyList()

}