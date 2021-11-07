package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitHubIngestionHookPayloadStatuses(
    private val gqlEnumIngestionHookPayloadStatus: GQLEnumIngestionHookPayloadStatus,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitHubIngestionHookPayloadStatuses")
        .description("List of statuses for the processing of ingestion hook payloads.")
        .type(listType(gqlEnumIngestionHookPayloadStatus.getTypeRef()))
        .dataFetcher {
            IngestionHookPayloadStatus.values()
        }
        .build()

}