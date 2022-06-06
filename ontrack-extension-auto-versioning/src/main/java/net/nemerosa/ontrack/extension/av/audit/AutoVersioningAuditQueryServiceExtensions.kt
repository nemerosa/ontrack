package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.structure.Branch

fun AutoVersioningAuditQueryService.getByUUID(branch: Branch, uuid: String) =
    findByUUID(branch, uuid) ?: throw AutoVersioningAuditEntryUUIDNotFoundException(uuid)
