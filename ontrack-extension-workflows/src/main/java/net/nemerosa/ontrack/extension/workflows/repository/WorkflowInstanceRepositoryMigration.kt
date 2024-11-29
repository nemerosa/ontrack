package net.nemerosa.ontrack.extension.workflows.repository

import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component

@Component
class WorkflowInstanceRepositoryMigration(
    private val storageService: StorageService,
) : StartupService {

    override fun getName(): String = "Cleanup of legacy workflows"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        storageService.clear("net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance")
    }
}