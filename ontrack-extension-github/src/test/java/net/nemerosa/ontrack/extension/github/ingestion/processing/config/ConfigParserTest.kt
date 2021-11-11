package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ConfigParserTest {

    @Test
    fun `Complete configuration`() {
        test(
            """
                general:
                    skipJobs: false
                    indexationInterval: 10
            """
        ) {
            assertEquals(false, it.general.skipJobs)
            assertEquals(10U, it.general.indexationInterval)
        }
    }

    @Test
    fun `Partial configuration with default value for skip jobs`() {
        test(
            """
                general:
                    indexationInterval: 10
            """
        ) {
            assertEquals(true, it.general.skipJobs)
            assertEquals(10U, it.general.indexationInterval)
        }
    }

    @Test
    fun `Partial configuration with default value for indexation interval`() {
        test(
            """
                general:
                    skipJobs: false
            """
        ) {
            assertEquals(false, it.general.skipJobs)
            assertEquals(30U, it.general.indexationInterval)
        }
    }

    fun test(
        yaml: String,
        code: (config: IngestionConfig) -> Unit,
    ) {
        val config = ConfigParser.parseYaml(yaml.trimIndent())
        assertNotNull(config, "Configuration could be parsed", code)
    }

}