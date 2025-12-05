package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

/**
 * Migration of the audit records into a table.
 */
@Component
class AutoVersioningAuditStoreMigration(
) : StartupService {

    override fun getName(): String = "Storing the auto-versioning audit records into a table"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        // TODO
    }
}