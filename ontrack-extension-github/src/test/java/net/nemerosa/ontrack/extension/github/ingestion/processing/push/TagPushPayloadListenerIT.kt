package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.*
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging.TagPushPayloadListener
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ContextConfiguration(classes = [ConfigLoaderServiceITMockConfig::class])
class TagPushPayloadListenerIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var listener: TagPushPayloadListener

    @Autowired
    private lateinit var configLoaderService: ConfigLoaderService

    @BeforeEach
    fun init() {
        onlyOneGitHubConfig()
        ConfigLoaderServiceITMockConfig.defaultIngestionConfig(configLoaderService)
    }

    @Test
    fun `Setting the release property based on tag using default strategy`() {
        asAdmin {
            project {
                branch {
                    build {
                        setCommitProperty(commitId)
                        // Payload processing
                        listener.process(payload(this), null)
                        // Checks the release property
                        assertEquals("1.0", releaseProperty)
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the release property based on tag using default strategy and specific branch`() {
        asAdmin {
            project {
                branch {
                    build {
                        setCommitProperty(commitId)
                        // Payload processing
                        listener.process(payload(this, baseRef = "refs/heads/${branch.name}"), null)
                        // Checks the release property
                        assertEquals("1.0", releaseProperty)
                    }
                }
            }
        }
    }

    @Test
    fun `Setting the release property based on tag using the promotion strategy and mismatch commit`() {
        ConfigLoaderServiceITMockConfig.customIngestionConfig(
            configLoaderService, IngestionConfig(
                tagging = IngestionTaggingConfig(
                    strategies = listOf(
                        IngestionTaggingStrategyConfig(
                            type = "promotion",
                            config = mapOf(
                                "name" to "BRONZE"
                            ).asJson()
                        )
                    )
                )
            )
        )
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    // Promoted build
                    val candidate = build {
                        setCommitProperty(commitId)
                        promote(bronze)
                    }
                    // New build
                    val newest = build {
                        setCommitProperty("yet-another-commit")
                    }
                    // Payload processing
                    listener.process(
                        payload(candidate, baseRef = "refs/heads/$name", commit = "another-commit", tag = "2.0"),
                        null
                    )
                    // Checks the release property
                    assertEquals("2.0", candidate.releaseProperty)
                    assertNull(newest.releaseProperty, "Newest build is untouched")
                }
            }
        }
    }

    private var Build.releaseProperty: String?
        get() = getProperty(this, ReleasePropertyType::class.java)?.name
        set(value) {
            if (value != null) {
                setProperty(this, ReleasePropertyType::class.java, ReleaseProperty(value))
            } else {
                deleteProperty(this, ReleasePropertyType::class.java)
            }
        }

    private fun Build.setCommitProperty(commit: String) {
        setProperty(this, GitCommitPropertyType::class.java, GitCommitProperty(commit))
    }

    private fun payload(
        build: Build,
        tag: String = "1.0",
        baseRef: String = "refs/heads/main",
        commit: String = commitId,
    ) = PushPayload(
        repository = IngestionHookFixtures.sampleRepository(
            repoName = build.project.name,
        ),
        ref = "refs/tags/$tag",
        baseRef = baseRef,
        headCommit = Commit(
            id = commit,
            message = "Commit 1",
            author = IngestionHookFixtures.sampleAuthor(),
        ),
    )

    private val commitId = "sample-commit"

}