package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import org.springframework.stereotype.Component

/**
 * Retrieves the last build having a given promotion.
 */
@Component
class PromotionTaggingStrategy : TaggingStrategy<PromotionTaggingStrategyConfig> {

    override val type: String = "promotion"

}