package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.settings.GitHubIngestionSettings
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.model.support.StorageService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class IngestionHookPayloadCleanupJobIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var job: IngestionHookPayloadCleanupJob

    @Autowired
    private lateinit var storage: IngestionHookPayloadStorage

    @Autowired
    private lateinit var storageService: StorageService

    @Test
    fun `Deletion of past payloads`() {
        withSettings<GitHubIngestionSettings> {
            asAdmin {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "not-used",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 30,
                    )
                )
            }
            // Clears all payloads first
            storageService.clear("github.IngestionHookPayload")
            // Creates payload before and after the retention period
            val ref = Time.now()
            asAdmin {
                (-20..-1).forEach { days ->
                    storage.store(
                        IngestionHookFixtures.sampleWorkflowRunIngestionPayload(
                            timestamp = ref.plusDays(days.toLong()).plusMinutes(1),
                            message = "$days",
                        ),
                        "source"
                    )
                }
            }
            // Running the cleanup job
            asAdmin {
                job.startingJobs.first().job.task.run(JobRunListener.out())
            }
            // Checks that only the most recent jobs have been kept
            val items = asAdmin { storage.list(size = Int.MAX_VALUE) }
            assertEquals(10, items.size)
            assertEquals(
                (-10..-1).map { it.toString() }.toSet(),
                items.map { it.message }.toSet()
            )
        }
    }

}