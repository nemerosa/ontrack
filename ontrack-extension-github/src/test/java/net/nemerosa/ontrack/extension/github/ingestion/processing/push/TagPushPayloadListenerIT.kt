package net.nemerosa.ontrack.extension.github.ingestion.processing.push

import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Commit
import net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging.TagPushPayloadListener
import net.nemerosa.ontrack.model.structure.Build
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TagPushPayloadListenerIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var listener: TagPushPayloadListener

    @BeforeEach
    fun init() {
        onlyOneGitHubConfig()
    }

    @Test
    fun `Setting the release property based on tag`() {
        asAdmin {
            project {
                branch {
                    build {
                        setProperty(this, GitCommitPropertyType::class.java, GitCommitProperty(commit = commitId))
                        // Payload processing
                        listener.process(payload(this), null)
                        // Checks the release property
                        assertNotNull(getProperty(this, ReleasePropertyType::class.java), "Release property has been set") {
                            assertEquals("1.0", it.name)
                        }
                    }
                }
            }
        }
    }

    private fun payload(build: Build) = PushPayload(
        repository = IngestionHookFixtures.sampleRepository(
            repoName = build.project.name,
        ),
        ref = "refs/tags/1.0",
        headCommit = Commit(
            id = commitId,
            message = "Commit 1",
            author = IngestionHookFixtures.sampleAuthor(),
        ),
    )

    private val commitId = "sample-commit"

}