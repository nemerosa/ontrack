package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigLoaderService
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.ConfigLoaderServiceITMockConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionRunConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging.TagPushPayloadListener
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import kotlin.test.assertEquals

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
        baseRef: String = "refs/heads/main",
    ) = PushPayload(
        repository = IngestionHookFixtures.sampleRepository(
            repoName = build.project.name,
        ),
        ref = "refs/tags/1.0",
        baseRef = baseRef,
        headCommit = Commit(
            id = commitId,
            message = "Commit 1",
            author = IngestionHookFixtures.sampleAuthor(),
        ),
    )

    private val commitId = "sample-commit"

}