package net.nemerosa.ontrack.service.dashboards

import net.nemerosa.ontrack.model.dashboards.WidgetLayout
import net.nemerosa.ontrack.model.support.StartupService
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DashboardWidgetHeight5Migration(
    private val storageService: StorageService,
    private val dashboardStorageService: DashboardStorageService,
) : StartupService {

    private val logger = LoggerFactory.getLogger(DashboardWidgetHeight5Migration::class.java)

    override fun getName(): String = "Height x 5 of dashboard widgets"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        // Checks if the migration was already done or not
        val migrated = storageService.find(
            store = DashboardWidgetHeight5Migration::class.java.name,
            key = "migration",
            type = DashboardWidgetHeight5MigrationStatus::class
        )?.migrated ?: false
        if (!migrated) {
            logger.info("Migrating of all dashboard widget heights x 5...")
            dashboardStorageService.migrateDashboards { dashboard ->
                dashboard.migrateWidgets { widget ->
                    widget.adaptLayout { layout ->
                        layout.run {
                            WidgetLayout(
                                x = x,
                                y = y * 5,
                                w = w,
                                h = h * 5,
                            )
                        }
                    }
                }
            }
            logger.info("Migration of all dashboard widget heights x 5 done.")
            storageService.store(
                store = DashboardWidgetHeight5Migration::class.java.name,
                key = "migration",
                data = DashboardWidgetHeight5MigrationStatus(migrated = true)
            )
        } else {
            logger.info("Dashboard widget heights x 5 was already done")
        }
    }

    data class DashboardWidgetHeight5MigrationStatus(
        val migrated: Boolean,
    )
}