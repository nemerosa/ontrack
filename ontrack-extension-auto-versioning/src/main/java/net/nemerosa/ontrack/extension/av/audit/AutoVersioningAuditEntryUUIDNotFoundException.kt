package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class AutoVersioningAuditEntryUUIDNotFoundException(uuid: String) : NotFoundException(
    """Auto versioning audit entry for UUID = $uuid cannot be found."""
)
