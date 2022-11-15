package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import io.mockk.every
import net.nemerosa.ontrack.extension.general.AutoPromotionProperty
import net.nemerosa.ontrack.extension.general.AutoPromotionPropertyType
import net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationConfig
import net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.ConfigParser
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import net.nemerosa.ontrack.model.structure.ValidationStamp
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.*

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class ConfigServiceIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Autowired
    private lateinit var configService: ConfigService

    @BeforeEach
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

    @Test
    @TestOnGitHub
    fun `Ingestion of validation stamp image`() {
        asAdmin {
            project {
                gitHubRealConfig()
                branch {
                    withConfigFile(
                        "/ingestion/config-validations-image.yml",
                        mapOf(
                            "#path" to "${githubTestEnv.organization}/${githubTestEnv.repository}/${githubTestEnv.paths.images.validation}"
                        )
                    ) {
                        checkValidationStampExists(this, "site") { vs ->
                            assertTrue(vs.isImage, "Image has been created for the validation stamp")
                            val image = structureService.getValidationStampImage(vs.id)
                            assertFalse(image.isEmpty, "Image has some content")
                            assertEquals("image/png", image.type)
                        }
                    }
                }
            }
        }
    }

    @Test
    @TestOnGitHub
    fun `Ingestion of promotion level image`() {
        asAdmin {
            project {
                gitHubRealConfig()
                branch {
                    withConfigFile(
                        "/ingestion/config-promotions-image.yml",
                        mapOf(
                            "#path" to "${githubTestEnv.organization}/${githubTestEnv.repository}/${githubTestEnv.paths.images.promotion}"
                        )
                    ) {
                        checkPromotionLevelExists(this, "iron") { pl ->
                            assertTrue(pl.isImage, "Image has been created for the promotion level")
                            val image = structureService.getPromotionLevelImage(pl.id)
                            assertFalse(image.isEmpty, "Image has some content")
                            assertEquals("image/png", image.type)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Creation of validation stamps`() {
        asAdmin {
            project {
                branch {
                    withConfigFile("/ingestion/config-validations-creation.yml") {
                        checkValidationStampExists(this, "unit-tests") { vs ->
                            assertDataType(vs) {
                                assertEquals(
                                    TestSummaryValidationDataType::class.java.name,
                                    it.descriptor.id
                                )
                                assertIs<TestSummaryValidationConfig>(it.config) { config ->
                                    assertTrue(config.warningIfSkipped, "Configuration is OK")
                                }
                            }
                        }
                        checkValidationStampExists(this, "e2e-tests") { vs ->
                            assertNoDataType(vs)
                        }
                        checkValidationStampExists(this, "sonarqube") { vs ->
                            assertNoDataType(vs)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Update of validation stamps`() {
        asAdmin {
            project {
                branch {
                    withConfigFile("/ingestion/config-validations-creation.yml") {
                        // Validation stamps are not setup
                        // Updating them
                        withConfigFile("/ingestion/config-validations-update.yml") {
                            checkValidationStampExists(this, "unit-tests") { vs ->
                                assertDataType(vs) {
                                    assertEquals(
                                        TestSummaryValidationDataType::class.java.name,
                                        it.descriptor.id
                                    )
                                    assertIs<TestSummaryValidationConfig>(it.config) { config ->
                                        assertTrue(config.warningIfSkipped, "Configuration is OK")
                                    }
                                }
                            }
                            checkValidationStampExists(this, "e2e-tests") { vs ->
                                assertDataType(vs) {
                                    assertEquals(
                                        TestSummaryValidationDataType::class.java.name,
                                        it.descriptor.id
                                    )
                                    assertIs<TestSummaryValidationConfig>(it.config) { config ->
                                        assertTrue(config.warningIfSkipped, "Configuration is OK")
                                    }
                                }
                            }
                            // Existing validation stamps are preserved
                            checkValidationStampExists(this, "sonarqube") { vs ->
                                assertNoDataType(vs)
                            }
                            // New validation stamp
                            checkValidationStampExists(this, "new-metrics") { vs ->
                                assertDataType(vs) {
                                    assertEquals(
                                        MetricsValidationDataType::class.java.name,
                                        it.descriptor.id
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Mix of validations and promotions`() {
        asAdmin {
            project {
                branch {
                    withConfigFile("/ingestion/config-validations-mix.yml") {

                        // Checking the validation stamps

                        checkValidationStampExists(this, "unit-tests") { vs ->
                            assertDataType(vs) {
                                assertEquals(
                                    TestSummaryValidationDataType::class.java.name,
                                    it.descriptor.id
                                )
                                assertIs<TestSummaryValidationConfig>(it.config) { config ->
                                    assertTrue(config.warningIfSkipped, "Configuration is OK")
                                }
                            }
                        }
                        checkValidationStampExists(this, "e2e-tests") { vs ->
                            assertNoDataType(vs)
                        }
                        checkValidationStampExists(this, "sonarqube") { vs ->
                            assertNoDataType(vs)
                        }

                        // Checking the promotion levels

                        checkPromotionLevelExists(this, "IRON") { iron ->
                            checkAutoPromotion(iron) {
                                assertEquals(
                                    setOf("unit-tests", "e2e-tests"),
                                    it.validationStamps.map { vs -> vs.name }.toSet()
                                )
                                assertTrue(it.promotionLevels.isEmpty(), "No promotion")
                            }
                        }

                        checkPromotionLevelExists(this, "SILVER") { silver ->
                            checkAutoPromotion(silver) {
                                assertEquals(
                                    setOf("sonarqube"),
                                    it.validationStamps.map { vs -> vs.name }.toSet()
                                )
                                assertEquals(
                                    setOf("SILVER"),
                                    it.promotionLevels.map { pl -> pl.name }.toSet()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkAutoPromotion(promotionLevel: PromotionLevel, code: (AutoPromotionProperty) -> Unit) {
        val property: AutoPromotionProperty? = getProperty(promotionLevel, AutoPromotionPropertyType::class.java)
        assertNotNull(property, "Auto promotion has been set on $promotionLevel") {
            code(it)
        }
    }

    private fun checkPromotionLevelExists(branch: Branch, name: String, code: (PromotionLevel) -> Unit) {
        val pl = structureService.getPromotionLevelListForBranch(branch.id).firstOrNull { it.name == name }
        assertNotNull(pl, "Promotion level $name has been created for $branch") {
            code(it)
        }
    }

    private fun assertDataType(vs: ValidationStamp, code: (ValidationDataTypeConfig<*>) -> Unit) {
        assertNotNull(vs.dataType, "Data type for $vs") {
            code(it)
        }
    }

    private fun assertNoDataType(vs: ValidationStamp) {
        assertNull(vs.dataType, "No data type for $vs")
    }

    fun checkValidationStampExists(branch: Branch, name: String, code: (vs: ValidationStamp) -> Unit) {
        val vs = structureService.getValidationStampListForBranch(branch.id).firstOrNull { it.name == name }
        assertNotNull(vs, "Validation stamp $name has been created for $branch") {
            code(it)
        }
    }

    fun Branch.withConfigFile(path: String, replacements: Map<String,String> = emptyMap(), code: () -> Unit) {
        val yaml = TestUtils.resourceString(path).run {
            replacements.entries.fold(this) { acc, (token, replacement) ->
                acc.replace(token, replacement)
            }
        }
        val config = ConfigParser.parseYaml(yaml)
        every {
            configLoaderService.loadConfig(
                this@withConfigFile,
                INGESTION_CONFIG_FILE_PATH
            )
        } returns config
        configService.loadAndSaveConfig(
            this,
            INGESTION_CONFIG_FILE_PATH,
        )
    }

    fun withSavedConfiguration(
        config: IngestionConfig = IngestionHookFixtures.sampleIngestionConfig(),
        test: (config: IngestionConfig, branch: Branch) -> Unit,
    ) {
        withLoadingConfiguration(config, existing = true) { _, branch ->
            configService.loadAndSaveConfig(
                branch,
                INGESTION_CONFIG_FILE_PATH,
            )
            // Testing
            test(config, branch)
        }
    }

    fun withLoadingConfiguration(
        config: IngestionConfig = IngestionHookFixtures.sampleIngestionConfig(),
        existing: Boolean,
        test: (config: IngestionConfig, branch: Branch) -> Unit,
    ) {
        asAdmin {
            project {
                branch {
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