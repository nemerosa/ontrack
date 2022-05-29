package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class PromotionTaggingStrategyIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var strategy: PromotionTaggingStrategy

    @Test
    fun `Parsing error when config is null`() {
        assertFailsWith<IllegalStateException> {
            strategy.parseAndValidate(null)
        }
    }

    @Test
    fun `Parsing error when config is not correct`() {
        assertFailsWith<JsonParseException> {
            strategy.parseAndValidate(
                mapOf("promotion" to "BRONZE").asJson()
            )
        }
    }

    @Test
    fun `Parsing of config`() {
        assertNotNull(
            strategy.parseAndValidate(
                mapOf("name" to "BRONZE").asJson()
            )
        ) { config ->
            assertEquals("BRONZE", config.name)
        }
    }

    @Test
    fun `When no configuration, no build can be found`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    build {
                        promote(bronze)
                    }
                    assertNull(
                        strategy.findBuild(
                            null,
                            this,
                            IngestionHookFixtures.samplePushPayload()
                        ),
                        "Cannot find build because configuration is not set"
                    )
                }
            }
        }
    }

    @Test
    fun `When no promotion exists, no build can be found`() {
        asAdmin {
            project {
                branch {
                    val other = promotionLevel()
                    build {
                        promote(other)
                    }
                    assertNull(
                        strategy.findBuild(
                            PromotionTaggingStrategyConfig("BRONZE"),
                            this,
                            IngestionHookFixtures.samplePushPayload()
                        ),
                        "Cannot find build because promotion does not exist"
                    )
                }
            }
        }
    }

    @Test
    fun `Returns the first promoted build`() {
        asAdmin {
            project {
                branch {
                    val bronze = promotionLevel("BRONZE")
                    val candidate = build {
                        promote(bronze)
                    }
                    build()
                    assertNotNull(
                        strategy.findBuild(
                            PromotionTaggingStrategyConfig("BRONZE"),
                            this,
                            IngestionHookFixtures.samplePushPayload()
                        ),
                        "First promoted build"
                    ) {
                        assertEquals(candidate, it)
                    }
                }
            }
        }
    }

}