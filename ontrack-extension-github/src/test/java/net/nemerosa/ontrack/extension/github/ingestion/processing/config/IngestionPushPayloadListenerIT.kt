package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.stale.StalePropertyType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
internal class IngestionPushPayloadListenerIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var ingestionPushPayloadListener: IngestionPushPayloadListener

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @Test
    fun `Configuration of the stale property for a branch by the ingestion configuration file`() {
        asAdmin {
            onlyOneGitHubConfig()
            val repository = uid("r")
            ConfigLoaderServiceITMockConfig.customIngestionConfig(configLoaderService, IngestionConfig(
                casc = IngestionCascConfig(
                    project = IngestionCascProjectConfig(
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
            ))
            ingestionPushPayloadListener.process(
                payload = IngestionHookFixtures.samplePushPayload(
                    repoName = repository,
                    ref = "refs/heads/main",
                    added = listOf(".github/ontrack/ingestion.yml"),
                ),
                configuration = null
            )
            assertNotNull(structureService.findBranchByName(repository, "main").getOrNull(),
                "Branch has been created") { branch ->
                // Gets its stale property
                assertNotNull(getProperty(branch.project, StalePropertyType::class.java),
                    "Stale property has been set on the project") { property ->
                    assertEquals(30, property.disablingDuration)
                    assertEquals(0, property.deletingDuration)
                    assertEquals(listOf("GOLD"), property.promotionsToKeep)
                    assertEquals("release/.*", property.includes)
                    assertEquals("release/1\\..*", property.excludes)
                }
            }
        }
    }

}