package net.nemerosa.ontrack.extension.av.audit

import kotlin.test.assertEquals

object AutoVersioningAuditTestFixtures {

    fun assertAudit(
        entry: AutoVersioningAuditEntry,
        vararg items: ReducedAuditItem,
    ) {
        assertEquals(
            entry.audit.map { it.reduce() },
            items.toList()
        )
    }

    data class ReducedAuditItem(
        val state: AutoVersioningAuditState,
        val data: Map<String, String>,
    )

    fun audit(state: AutoVersioningAuditState, vararg data: Pair<String, String>) =
        ReducedAuditItem(state, data.toMap())

    fun AutoVersioningAuditEntryState.reduce() = ReducedAuditItem(
        state = state,
        data = data
    )

}