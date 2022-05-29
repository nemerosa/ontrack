package net.nemerosa.ontrack.extension.github.ingestion.processing.push.tagging

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.github.ingestion.processing.config.IngestionTaggingStrategyConfig

class TaggingStrategyConfigParsingException(
    config: IngestionTaggingStrategyConfig,
    ex: Exception,
) : BaseException(
    """Cannot parse tagging strategy config [${config.config}] for type ${config.type}.""",
    ex
)