package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType
import net.nemerosa.ontrack.extension.general.ReleaseValidationPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigCascSetup
import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigSetup
import net.nemerosa.ontrack.extension.github.ingestion.config.parser.ConfigParsingException
import net.nemerosa.ontrack.extension.stale.StalePropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.jvm.optionals.getOrNull
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
internal class IngestionPushPayloadListenerIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionPushPayloadListener: IngestionPushPayloadListener

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Test
    fun `Configuration parsing error`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.failedIngestionConfig(configLoaderService)
            assertFailsWith<ConfigParsingException> {
                ingestionPushPayloadListener.process(
                    payload = IngestionHookFixtures.samplePushPayload(
                        repoName = repository,
                        ref = "refs/heads/main",
                        modified = listOf(".github/ontrack/ingestion.yml"),
                    ),
                    configuration = null
                )
            }
        }
    }

    @Test
    fun `Configuration of the stale property by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService, IngestionConfig(
                    setup = IngestionConfigSetup(
                        project = IngestionConfigCascSetup(
                            casc = mapOf(
                                "properties" to mapOf(
                                    "staleProperty" to mapOf(
                                        "disablingDuration" to 30,
                                        "deletingDuration" to 0,
                                        "promotionsToKeep" to listOf("GOLD"),
                                        "includes" to "release/.*",
                                        "excludes" to "release/1\\..*",
                                    )
                                )
                            ).asJson()
                        )
                    ),
                )
            )
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/main",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(
                structureService.findBranchByName(repository, "main").getOrNull(),
                "Branch has been created"
            ) { branch ->
                // Gets its stale property
                assertNotNull(
                    getProperty(branch.project, StalePropertyType::class.java),
                    "Stale property has been set on the project"
                ) { property ->
                    assertEquals(30, property.disablingDuration)
                    assertEquals(0, property.deletingDuration)
                    assertEquals(listOf("GOLD"), property.promotionsToKeep)
                    assertEquals("release/.*", property.includes)
                    assertEquals("release/1\\..*", property.excludes)
                }
            }
        }
    }

    @Test
    fun `Configuration of the build link display property by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService, IngestionConfig(
                    setup = IngestionConfigSetup(
                        project = IngestionConfigCascSetup(
                            casc = mapOf(
                                "properties" to mapOf(
                                    "buildLinkDisplayProperty" to mapOf(
                                        "useLabel" to true
                                    )
                                )
                            ).asJson()
                        )
                    )
                )
            )
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/main",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(
                structureService.findBranchByName(repository, "main").getOrNull(),
                "Branch has been created"
            ) { branch ->
                // Gets its build link display property
                assertNotNull(
                    getProperty(branch.project, BuildLinkDisplayPropertyType::class.java),
                    "Build link display property has been set on the project"
                ) { property ->
                    assertEquals(true, property.useLabel)
                }
            }
        }
    }

    @Test
    fun `Configuration of the release validation property on a branch by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("repo_")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService, IngestionConfig(
                    setup = IngestionConfigSetup(
                        branch = IngestionConfigCascSetup(
                            casc = mapOf(
                                "properties" to mapOf(
                                    "releaseValidationProperty" to mapOf(
                                        "validation" to "labelled"
                                    )
                                )
                            ).asJson()
                        )
                    )
                )
            )
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/main",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(
                structureService.findBranchByName(repository, "main").getOrNull(),
                "Branch has been created"
            ) { branch ->
                // Gets its release validation display property
                assertNotNull(
                    getProperty(branch, ReleaseValidationPropertyType::class.java),
                    "Release validation property has been set on the branch"
                ) { property ->
                    assertEquals("labelled", property.validation)
                }
            }
        }
    }

    @Test
    fun `Configuration of the stale property skipped for a non included branch by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService, IngestionConfig(
                    setup = IngestionConfigSetup(
                        project = IngestionConfigCascSetup(
                            casc = mapOf(
                                "properties" to mapOf(
                                    "staleProperty" to mapOf(
                                        "disablingDuration" to 30,
                                        "deletingDuration" to 0,
                                        "promotionsToKeep" to listOf("GOLD"),
                                    )
                                )
                            ).asJson()
                        )
                    )
                )
            )
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/feature/not-main",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(
                structureService.findBranchByName(repository, "feature-not-main").getOrNull(),
                "Branch has been created"
            ) { branch ->
                // Gets its stale property --> not created since branch is excluded
                assertNull(
                    getProperty(branch.project, StalePropertyType::class.java),
                    "Stale property has not been set on the project"
                )
            }
        }
    }

    @Test
    fun `Configuration of the stale property skipped for an excluded branch by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(
                configLoaderService, IngestionConfig(
                    setup = IngestionConfigSetup(
                        project = IngestionConfigCascSetup(
                            includes = "main|release-.*",
                            excludes = "release-1\\..*",
                            casc = mapOf(
                                "properties" to mapOf(
                                    "staleProperty" to mapOf(
                                        "disablingDuration" to 30,
                                        "deletingDuration" to 0,
                                        "promotionsToKeep" to listOf("GOLD"),
                                        "includes" to "release/.*",
                                        "excludes" to "release/1\\..*",
                                    )
                                )
                            ).asJson()
                        )
                    )
                )
            )
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/release/1.0",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(
                structureService.findBranchByName(repository, "release-1.0").getOrNull(),
                "Branch has been created"
            ) { branch ->
                // Gets its stale property --> not created since branch is excluded
                assertNull(
                    getProperty(branch.project, StalePropertyType::class.java),
                    "Stale property has not been set on the project"
                )
            }
        }
    }

}