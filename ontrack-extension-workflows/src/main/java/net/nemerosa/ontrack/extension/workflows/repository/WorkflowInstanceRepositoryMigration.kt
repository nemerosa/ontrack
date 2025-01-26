package net.nemerosa.ontrack.extension.workflows.repository

import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import net.nemerosa.ontrack.repository.support.AbstractJdbcRepository
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class WorkflowInstanceRepositoryMigration(
    dataSource: DataSource,
    private val storageService: StorageService,
) : StartupService, AbstractJdbcRepository(dataSource) {

    override fun getName(): String = "Cleanup of legacy workflows"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        // Removing all instance records
        storageService.clear("net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance")
        // Removing all notification records created before the 4.11 migration
        jdbcTemplate!!.update(
            """
                DELETE FROM STORAGE
                WHERE STORE = 'net.nemerosa.ontrack.extension.notifications.recording.NotificationRecord'
                AND DATA->>'channel' = 'workflow'
                AND NOT (DATA->'channelConfig' ? 'pauseMs')
            """.trimIndent()
        )
    }
}