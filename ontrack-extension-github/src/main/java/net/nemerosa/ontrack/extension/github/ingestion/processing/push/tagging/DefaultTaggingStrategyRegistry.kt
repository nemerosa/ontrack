package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingConfig
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingStrategyConfig
import org.springframework.stereotype.Component

@Component
class DefaultTaggingStrategyRegistry(
    strategies: List<TaggingStrategy<*>>,
    private val commitPropertyTaggingStrategy: CommitPropertyTaggingStrategy,
) : TaggingStrategyRegistry {

    private val index = strategies.associateBy { it.type }

    override fun getTaggingStrategies(tagging: IngestionTaggingConfig): List<ConfiguredTaggingStrategy<*>> {
        val list = tagging.strategies.map {
            parseTaggingStrategy<Any>(it)
        }
        return if (tagging.commitProperty && list.none { it.strategy.type == CommitPropertyTaggingStrategy.COMMIT_PROPERTY_TAGGING_STRATEGY_TYPE }) {
            list + ConfiguredTaggingStrategy(commitPropertyTaggingStrategy, null)
        } else {
            list
        }
    }

    internal fun <C> parseTaggingStrategy(config: IngestionTaggingStrategyConfig): ConfiguredTaggingStrategy<C> {
        // Getting the strategy
        @Suppress("UNCHECKED_CAST")
        val strategy: TaggingStrategy<C> = index[config.type]
                as? TaggingStrategy<C>
            ?: throw TaggingStrategyNotFoundException(config.type)
        // Parsing the configuration
        val strategyConfig = try {
            strategy.parseAndValidate(config.config)
        } catch (ex: Exception) {
            throw TaggingStrategyConfigParsingException(config, ex)
        }
        // OK
        return ConfiguredTaggingStrategy(strategy, strategyConfig)
    }

}