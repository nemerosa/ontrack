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
                steps:
                    - name: Step name
                      validation: my-validation
                      validationJobPrefix: false
                      description: My description
                jobs:
                    - name: Job name
                      validation: my-job
                      description: My description
            """
        ) {
            assertEquals(false, it.general.skipJobs)

            assertEquals(1, it.steps.size)
            assertEquals("Step name", it.steps.first().name)
            assertEquals("my-validation", it.steps.first().validation)
            assertEquals(false, it.steps.first().validationJobPrefix)
            assertEquals("My description", it.steps.first().description)

            assertEquals(1, it.jobs.size)
            assertEquals("Job name", it.jobs.first().name)
            assertEquals("my-job", it.jobs.first().validation)
            assertEquals("My description", it.jobs.first().description)
        }
    }

    @Test
    fun `Partial configuration with default values`() {
        test(
            """
                steps:
                    - name: Step name
                jobs:
                    - name: Job name
            """
        ) {
            assertEquals(true, it.general.skipJobs)

            assertEquals(1, it.steps.size)
            assertEquals("Step name", it.steps.first().name)
            assertEquals(null, it.steps.first().validation)
            assertEquals(true, it.steps.first().validationJobPrefix)
            assertEquals(null, it.steps.first().description)

            assertEquals(1, it.jobs.size)
            assertEquals("Job name", it.jobs.first().name)
            assertEquals(null, it.jobs.first().validation)
            assertEquals(null, it.jobs.first().description)
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