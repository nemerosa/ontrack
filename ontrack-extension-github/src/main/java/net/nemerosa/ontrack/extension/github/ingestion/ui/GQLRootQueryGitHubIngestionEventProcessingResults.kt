package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitHubIngestionEventProcessingResults(
    private val gqlEnumIngestionEventProcessingResult: GQLEnumIngestionEventProcessingResult,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitHubIngestionEventProcessingResults")
        .description("List of possible outcomes for the processing of ingestion hook payloads.")
        .type(listType(gqlEnumIngestionEventProcessingResult.getTypeRef()))
        .dataFetcher {
            IngestionEventProcessingResult.values()
        }
        .build()

}