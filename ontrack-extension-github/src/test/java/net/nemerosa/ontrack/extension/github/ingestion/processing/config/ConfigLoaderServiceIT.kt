package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertFalse

class ConfigLoaderServiceIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Test
    fun `Getting the ingestion configuration from a real project`() {
        project {
            gitHubRealConfig()
            branch {
                gitRealConfig()
                val config = configLoaderService.loadConfig(this, INGESTION_CONFIG_FILE_PATH)
                assertNotNull(config, "Ingestion configuration was loaded") {
                    assertFalse(it.general.skipJobs, "Skip jobs property has been set to false")
                }
            }
        }
    }
}