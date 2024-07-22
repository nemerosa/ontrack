package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.AutoVersioningAuditEntryFragment

fun AutoVersioningAuditEntryFragment.toAutoVersioningAuditEntry() = AutoVersioningAuditEntry(
    order = AutoVersioningOrder(
        uuid = order().uuid(),
    ),
    running = running() ?: false,
    mostRecentState = AutoVersioningAuditEntryState(
        state = mostRecentState().state().name,
        data = mostRecentState().data(),
    ),
    audit = audit().map { auditEntry ->
        AutoVersioningAuditEntryState(
            state = auditEntry.state().name,
            data = auditEntry.data(),
        )
    },
    routing = routing(),
    queue = queue(),
)
