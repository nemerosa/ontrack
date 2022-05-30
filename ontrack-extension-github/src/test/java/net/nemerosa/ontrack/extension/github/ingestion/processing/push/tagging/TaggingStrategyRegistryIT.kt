package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingStrategyConfig
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

internal class TaggingStrategyRegistryIT : AbstractIngestionTestSupport() {

    @Autowired
    private lateinit var registry: DefaultTaggingStrategyRegistry

    @Test
    fun `Tagging strategy not found`() {
        assertFailsWith<TaggingStrategyNotFoundException> {
            registry.parseTaggingStrategy<Any>(
                IngestionTaggingStrategyConfig(
                    type = "unknown",
                    config = null,
                )
            )
        }
    }

    @Test
    fun `Commit property strategy parsing`() {
        val o = registry.parseTaggingStrategy<Any>(
            IngestionTaggingStrategyConfig(
                type = "commit-property",
                config = null,
            )
        )
        assertIs<CommitPropertyTaggingStrategy>(o.strategy)
        assertNull(o.config, "No configuration")
    }

    @Test
    fun `Promotion strategy parsing`() {
        val o = registry.parseTaggingStrategy<Any>(
            IngestionTaggingStrategyConfig(
                type = "promotion",
                config = mapOf(
                    "name" to "BRONZE"
                ).asJson(),
            )
        )
        assertIs<PromotionTaggingStrategy>(o.strategy)
        assertIs<PromotionTaggingStrategyConfig>(o.config) {
            assertEquals("BRONZE", it.name)
        }
    }

    @Test
    fun `Promotion strategy parsing error`() {
        assertFailsWith<TaggingStrategyConfigParsingException> {
            registry.parseTaggingStrategy<Any>(
                IngestionTaggingStrategyConfig(
                    type = "promotion",
                    config = mapOf(
                        "promotion" to "BRONZE"
                    ).asJson(),
                )
            )
        }
    }

    @Test
    fun `Promotion strategy parsing error when no config`() {
        assertFailsWith<TaggingStrategyConfigParsingException> {
            registry.parseTaggingStrategy<Any>(
                IngestionTaggingStrategyConfig(
                    type = "promotion",
                    config = null,
                )
            )
        }
    }

    @Test
    fun `Default tagging strategy only`() {
        val strategies = registry.getTaggingStrategies(IngestionTaggingConfig())
        assertEquals(1, strategies.size)
        val strategy = strategies.first()
        assertIs<CommitPropertyTaggingStrategy>(strategy.strategy)
        assertNull(strategy.config)
    }

    @Test
    fun `Default tagging strategy last`() {
        val strategies = registry.getTaggingStrategies(
            IngestionTaggingConfig(
                strategies = listOf(
                    IngestionTaggingStrategyConfig(
                        type = "promotion",
                        config = mapOf("name" to "BRONZE").asJson()
                    )
                )
            )
        )
        assertEquals(2, strategies.size)
        strategies[0].let {
            assertIs<PromotionTaggingStrategy>(it.strategy)
            assertIs<PromotionTaggingStrategyConfig>(it.config) { config ->
                assertEquals("BRONZE", config.name)
            }
        }
        strategies[1].let {
            assertIs<CommitPropertyTaggingStrategy>(it.strategy)
            assertNull(it.config)
        }
    }

    @Test
    fun `Default tagging strategy omitted`() {
        val strategies = registry.getTaggingStrategies(
            IngestionTaggingConfig(
                commitProperty = false,
                strategies = listOf(
                    IngestionTaggingStrategyConfig(
                        type = "promotion",
                        config = mapOf("name" to "BRONZE").asJson()
                    )
                )
            )
        )
        assertEquals(1, strategies.size)
        strategies[0].let {
            assertIs<PromotionTaggingStrategy>(it.strategy)
            assertIs<PromotionTaggingStrategyConfig>(it.config) { config ->
                assertEquals("BRONZE", config.name)
            }
        }
    }

    @Test
    fun `Default tagging strategy omitted but added explicitly`() {
        val strategies = registry.getTaggingStrategies(
            IngestionTaggingConfig(
                commitProperty = false,
                strategies = listOf(
                    IngestionTaggingStrategyConfig(
                        type = "promotion",
                        config = mapOf("name" to "BRONZE").asJson()
                    ),
                    IngestionTaggingStrategyConfig(
                        type = "commit-property",
                        config = null
                    ),
                )
            )
        )
        assertEquals(2, strategies.size)
        strategies[0].let {
            assertIs<PromotionTaggingStrategy>(it.strategy)
            assertIs<PromotionTaggingStrategyConfig>(it.config) { config ->
                assertEquals("BRONZE", config.name)
            }
        }
        strategies[1].let {
            assertIs<CommitPropertyTaggingStrategy>(it.strategy)
            assertNull(it.config)
        }
    }

    @Test
    fun `Default tagging strategy included but added explicitly`() {
        val strategies = registry.getTaggingStrategies(
            IngestionTaggingConfig(
                commitProperty = true,
                strategies = listOf(
                    IngestionTaggingStrategyConfig(
                        type = "promotion",
                        config = mapOf("name" to "BRONZE").asJson()
                    ),
                    IngestionTaggingStrategyConfig(
                        type = "commit-property",
                        config = null
                    ),
                )
            )
        )
        assertEquals(2, strategies.size)
        strategies[0].let {
            assertIs<PromotionTaggingStrategy>(it.strategy)
            assertIs<PromotionTaggingStrategyConfig>(it.config) { config ->
                assertEquals("BRONZE", config.name)
            }
        }
        strategies[1].let {
            assertIs<CommitPropertyTaggingStrategy>(it.strategy)
            assertNull(it.config)
        }
    }

}