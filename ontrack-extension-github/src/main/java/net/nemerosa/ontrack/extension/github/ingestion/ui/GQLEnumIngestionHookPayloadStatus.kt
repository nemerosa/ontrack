package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumIngestionHookPayloadStatus : AbstractGQLEnum<IngestionHookPayloadStatus>(
    IngestionHookPayloadStatus::class,
    IngestionHookPayloadStatus.values(),
    "Status of the processing of the payload"
)
