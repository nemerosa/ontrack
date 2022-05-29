package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.git.property.GitCommitProperty
import net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType
import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class CommitPropertyTaggingStrategyIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var strategy: CommitPropertyTaggingStrategy

    @Test
    fun `Parsing always returns null`() {
        assertNull(strategy.parseAndValidate(mapOf("any" to "any").asJson()))
    }

    @Test
    fun `Build with commit`() {
        val commit = "abcd123"
        asAdmin {
            project {
                branch {
                    build()
                    val target = build {
                        setProperty(
                            this,
                            GitCommitPropertyType::class.java,
                            GitCommitProperty(commit)
                        )
                    }
                    build()
                    // Looking for the build
                    assertNotNull(
                        strategy.findBuild(
                            null, this, IngestionHookFixtures.samplePushPayload(
                                id = commit,
                            )
                        )
                    ) {
                        assertEquals(target, it)
                    }
                }
            }
        }
    }

    @Test
    fun `Build with commit not found`() {
        val commit = "abcd123"
        asAdmin {
            project {
                branch {
                    build()
                    val target = build {
                        setProperty(
                            this,
                            GitCommitPropertyType::class.java,
                            GitCommitProperty(commit)
                        )
                    }
                    build()
                    // Looking for the build
                    assertNull(
                        strategy.findBuild(
                            null, this, IngestionHookFixtures.samplePushPayload(
                                id = "another-commit",
                            )
                        ),
                        "Build not found"
                    )
                }
            }
        }
    }

}