package net.nemerosa.ontrack.extension.av.audit

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.av.AbstractAutoVersioningTestSupport
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.settings.AutoVersioningSettings
import net.nemerosa.ontrack.job.JobRunListener
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AutoVersioningAuditCleanupJobIT : AbstractAutoVersioningTestSupport() {

    @Autowired
    private lateinit var autoVersioningAuditService: AutoVersioningAuditService

    @Autowired
    private lateinit var autoVersioningAuditStore: AutoVersioningAuditStore

    @Autowired
    private lateinit var autoVersioningAuditCleanupJob: AutoVersioningAuditCleanupJob

    @Autowired
    private lateinit var autoVersioningAuditQueryService: AutoVersioningAuditQueryService

    @Test
    fun `Cleanup of stopped orders`() {
        val source = project()
        project {
            branch {
                // Set the retention period to 7 + 20 days
                val oldSettings = settingsService.getCachedSettings(AutoVersioningSettings::class.java)
                try {
                    val newSettings = newSettings()
                    settingsManagerService.saveSettings(newSettings)
                    // Creates orders older than 7 days
                    withSignatureDaysOlder(14) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "1.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                            }
                        }
                    }
                    // Creates orders younger than 7 days
                    withSignatureDaysOlder(5) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                            }
                        }
                    }
                    // Runs the cleanup job
                    autoVersioningAuditCleanupJob.startingJobs.first().job.task.run(JobRunListener.out())
                    // Checks only the last entries are kept
                    val entries = autoVersioningAuditQueryService.findByFilter(
                        AutoVersioningAuditQueryFilter(
                            branch = this.name,
                            project = this.project.name
                        )
                    )
                    assertEquals(
                        5,
                        entries.size
                    )
                    assertTrue(
                        entries.all {
                            it.order.targetVersion.startsWith("2.0")
                        },
                        "Most recent entries are kept, older ones are deleted"
                    )
                } finally {
                    settingsManagerService.saveSettings(oldSettings)
                }
            }
        }
    }

    @Test
    fun `Only stopped orders are cleaned`() {
        val source = project()
        project {
            branch {
                // Set the retention period to 7 + 20 days
                val oldSettings = settingsService.getCachedSettings(AutoVersioningSettings::class.java)
                try {
                    val newSettings = newSettings()
                    settingsManagerService.saveSettings(newSettings)
                    // Creates stopped orders older than 7 days
                    withSignatureDaysOlder(14) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "1.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                            }
                        }
                    }
                    // Creates running orders older than 7 days
                    withSignatureDaysOlder(14) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                // Still processing
                            }
                        }
                    }
                    // Runs the cleanup job
                    autoVersioningAuditCleanupJob.startingJobs.first().job.task.run(JobRunListener.out())
                    // Checks only the last entries are kept
                    val entries = autoVersioningAuditQueryService.findByFilter(
                        AutoVersioningAuditQueryFilter(
                            branch = this.name,
                            project = this.project.name
                        )
                    )
                    assertEquals(
                        5,
                        entries.size
                    )
                    assertTrue(
                        entries.all {
                            it.order.targetVersion.startsWith("2.0")
                        },
                        "Running entries are kept, stopped ones are deleted"
                    )
                } finally {
                    settingsManagerService.saveSettings(oldSettings)
                }
            }
        }
    }

    @Test
    fun `Complete cleanup after some time`() {
        val source = project()
        project {
            branch {
                // Set the retention period to 7 + 20 days
                val oldSettings = settingsService.getCachedSettings(AutoVersioningSettings::class.java)
                try {
                    val newSettings = newSettings()
                    settingsManagerService.saveSettings(newSettings)
                    // Creates stopped orders older than 7 days
                    withSignatureDaysOlder(14) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "1.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                autoVersioningAuditService.onPRMerged(this, "branch", "#1", "uri:1")
                            }
                        }
                    }
                    // Creates running orders younger than 7 + 20 days
                    withSignatureDaysOlder(20) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "2.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                // Still processing
                            }
                        }
                    }
                    // Creates running orders older than 7 + 20 days
                    withSignatureDaysOlder(30) {
                        (1..5).map {
                            createOrder(sourceProject = source.name, targetVersion = "3.0.$it").apply {
                                autoVersioningAuditService.onQueuing(this, "routing")
                                autoVersioningAuditService.onReceived(this, "queue")
                                // Still processing
                            }
                        }
                    }
                    // Runs the cleanup job
                    autoVersioningAuditCleanupJob.startingJobs.first().job.task.run(JobRunListener.out())
                    // Checks that the only running entries which are kept are the ones younger than 7 + 20 days
                    val entries = autoVersioningAuditQueryService.findByFilter(
                        AutoVersioningAuditQueryFilter(
                            branch = this.name,
                            project = this.project.name
                        )
                    )
                    assertEquals(
                        5,
                        entries.size
                    )
                    assertTrue(
                        entries.all {
                            it.order.targetVersion.startsWith("2.0")
                        },
                        "Recent running entries are kept, stopped ones are deleted, older running entries are gone"
                    )
                } finally {
                    settingsManagerService.saveSettings(oldSettings)
                }
            }
        }
    }

    private fun newSettings() = AutoVersioningSettings(
        enabled = true,
        auditRetentionDuration = Duration.ofDays(7),
        auditCleanupDuration = Duration.ofDays(20)
    )

    private fun <T> withSignatureDaysOlder(days: Int, code: () -> T): T {
        val impl = autoVersioningAuditStore as AutoVersioningAuditStoreImpl
        val oldProvider = impl.signatureProvider
        return try {
            impl.signatureProvider = {
                securityService.currentSignature.withTime(
                    Time.now().minusDays(days.toLong())
                )
            }
            code()
        } finally {
            impl.signatureProvider = oldProvider
        }
    }

}