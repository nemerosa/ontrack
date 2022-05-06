package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumIngestionEventProcessingResult : AbstractGQLEnum<IngestionEventProcessingResult>(
    IngestionEventProcessingResult::class,
    IngestionEventProcessingResult.values(),
    "Outcome of the processing of the payload"
)
