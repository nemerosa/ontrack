package net.nemerosa.ontrack.extension.workflows.repository

import net.nemerosa.ontrack.model.support.StartupService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class WorkflowInstanceRepositoryStatusMigration(
    private val workflowInstanceRepository: WorkflowInstanceRepository,
) : StartupService {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowInstanceRepositoryStatusMigration::class.java)

    override fun getName(): String = "Persisting the workflow instance statuses"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        val count = workflowInstanceRepository.migrateStatuses()
        logger.info("Migrated $count workflow instances")
    }
}