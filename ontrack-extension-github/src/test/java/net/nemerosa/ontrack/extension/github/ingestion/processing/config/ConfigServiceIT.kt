package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class ConfigServiceIT : AbstractIngestionTestSupport() {

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
        withSavedConfiguration { config, repository, branch ->
            // Loading the configuration
            assertNotNull(configService.findConfig(repository, branch), "Configuration saved") {
                assertEquals(config, it)
            }
        }
    }

    @Test
    fun `Removing the configuration`() {
        withSavedConfiguration { _, repository, branch ->
            // Loading the configuration
            assertNotNull(configService.findConfig(repository, branch), "Configuration saved")
            // Removing the configuration
            configService.removeConfig(repository, branch)
            // Loading the configuration
            assertNull(configService.findConfig(repository, branch), "Configuration removed")
        }
    }

    @Test
    fun `Get or load with already saved configuration`() {
        withSavedConfiguration { saved, repository, branch ->
            val config = configService.getOrLoadConfig(repository, branch, INGESTION_CONFIG_FILE_PATH)
            assertEquals(saved, config)
        }
    }

    @Test
    fun `Get or load with loading configuration`() {
        withLoadingConfiguration(existing = true) { config ->
            val loaded = configService.getOrLoadConfig(
                IngestionHookFixtures.sampleRepository(),
                IngestionHookFixtures.sampleBranch,
                INGESTION_CONFIG_FILE_PATH
            )
            assertEquals(config, loaded)
        }
    }

    @Test
    fun `Get or load with loading non existent configuration`() {
        withLoadingConfiguration(existing = false) {
            val loaded = configService.getOrLoadConfig(
                IngestionHookFixtures.sampleRepository(),
                IngestionHookFixtures.sampleBranch,
                INGESTION_CONFIG_FILE_PATH
            )
            assertEquals(IngestionConfig(), loaded, "Loaded configuration is the default configuration")
        }
    }

    fun withSavedConfiguration(
        test: (config: IngestionConfig, repository: Repository, branch: String) -> Unit
    ) {
        withLoadingConfiguration(existing = true) { config ->
            val repository = IngestionHookFixtures.sampleRepository()
            val branch = IngestionHookFixtures.sampleBranch
            configService.saveConfig(
                repository,
                branch,
                INGESTION_CONFIG_FILE_PATH,
            )
            // Testing
            test(config, repository, branch)
        }
    }

    fun withLoadingConfiguration(
        existing: Boolean,
        test: (config: IngestionConfig) -> Unit
    ) {
        val config = IngestionHookFixtures.sampleIngestionConfig()
        if (existing) {
            every {
                configLoaderService.loadConfig(
                    any(),
                    INGESTION_CONFIG_FILE_PATH
                )
            } returns config
        } else {
            every {
                configLoaderService.loadConfig(
                    any(),
                    INGESTION_CONFIG_FILE_PATH
                )
            } returns null
        }
        asAdmin {
            test(config)
        }
    }

}