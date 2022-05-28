package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import org.springframework.stereotype.Component

@Component
class CommitPropertyTaggingStrategy : TaggingStrategy<Any> {

    override val type: String = COMMIT_PROPERTY_TAGGING_STRATEGY_TYPE

    companion object {
        const val COMMIT_PROPERTY_TAGGING_STRATEGY_TYPE = "commit-strategy"
    }
}