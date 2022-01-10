package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestJUnit4Support
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.model.structure.Branch
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class ConfigServiceIT : AbstractIngestionTestJUnit4Support() {

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Autowired
    private lateinit var configService: ConfigService

    @Before
    fun before() {
        onlyOneGitHubConfig()
    }

    @Test
    fun `Saving the configuration`() {
        withSavedConfiguration { config, branch ->
            // Loading the configuration
            assertNotNull(configService.findConfig(branch), "Configuration saved") {
                assertEquals(config, it)
            }
        }
    }

    @Test
    fun `Removing the configuration`() {
        withSavedConfiguration { _, branch ->
            // Loading the configuration
            assertNotNull(configService.findConfig(branch), "Configuration saved")
            // Removing the configuration
            configService.removeConfig(branch)
            // Loading the configuration
            assertNull(configService.findConfig(branch), "Configuration removed")
        }
    }

    @Test
    fun `Get or load with already saved configuration`() {
        withSavedConfiguration { saved, branch ->
            val config = configService.getOrLoadConfig(branch, INGESTION_CONFIG_FILE_PATH)
            assertEquals(saved, config)
        }
    }

    @Test
    fun `Get or load with loading configuration`() {
        withLoadingConfiguration(existing = true) { config, branch ->
            val loaded = configService.getOrLoadConfig(
                branch,
                INGESTION_CONFIG_FILE_PATH
            )
            assertEquals(config, loaded)
        }
    }

    @Test
    fun `Get or load with loading non existent configuration`() {
        withLoadingConfiguration(existing = false) { _, branch ->
            val loaded = configService.getOrLoadConfig(
                branch,
                INGESTION_CONFIG_FILE_PATH
            )
            assertEquals(IngestionConfig(), loaded, "Loaded configuration is the default configuration")
        }
    }

    fun withSavedConfiguration(
        test: (config: IngestionConfig, branch: Branch) -> Unit
    ) {
        withLoadingConfiguration(existing = true) { config, branch ->
            configService.loadAndSaveConfig(
                branch,
                INGESTION_CONFIG_FILE_PATH,
            )
            // Testing
            test(config, branch)
        }
    }

    fun withLoadingConfiguration(
        existing: Boolean,
        test: (config: IngestionConfig, branch: Branch) -> Unit
    ) {
        asAdmin {
            project {
                branch {
                    val config = IngestionHookFixtures.sampleIngestionConfig()
                    if (existing) {
                        every {
                            configLoaderService.loadConfig(
                                this@branch,
                                INGESTION_CONFIG_FILE_PATH
                            )
                        } returns config
                    } else {
                        every {
                            configLoaderService.loadConfig(
                                this@branch,
                                INGESTION_CONFIG_FILE_PATH
                            )
                        } returns null
                    }
                    test(config, this)
                }
            }
        }
    }

}