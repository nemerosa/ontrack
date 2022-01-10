package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.it.AbstractDSLTestJUnit4Support
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SCMCatalogImportJobIT : AbstractDSLTestJUnit4Support() {

    @Autowired
    private lateinit var scmCatalogImportJob: SCMCatalogImportJob

    @Test
    fun `Checking that the state of the job depends on the settings`() {
        withSettings<SCMCatalogSyncSettings> {
            val job = scmCatalogImportJob.startingJobs.first().job

            asAdmin {
                settingsManagerService.saveSettings(
                    SCMCatalogSyncSettings(syncEnabled = false)
                )
            }

            assertTrue(job.isDisabled, "Job is disabled because sync settings are disabled")

            asAdmin {
                settingsManagerService.saveSettings(
                    SCMCatalogSyncSettings(syncEnabled = true)
                )
            }

            assertFalse(job.isDisabled, "Job is enabled because sync settings are enabled")

        }
    }

}