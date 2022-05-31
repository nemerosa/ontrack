package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class LatestTaggingStrategyIT: AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var strategy: LatestTaggingStrategy

    @Test
    fun `Returns null when no build`() {
        asAdmin {
            project {
                branch {
                    assertNull(
                        strategy.findBuild(null, this, IngestionHookFixtures.samplePushPayload()),
                        "No build on the branch"
                    )
                }
            }
        }
    }

    @Test
    fun `Latest build`() {
        asAdmin {
            project {
                branch {
                    repeat(2) { build() }
                    val build = build()
                    assertEquals(
                        build,
                        strategy.findBuild(null, this, IngestionHookFixtures.samplePushPayload()),
                        "Latest build on the branch"
                    )
                }
            }
        }
    }

}