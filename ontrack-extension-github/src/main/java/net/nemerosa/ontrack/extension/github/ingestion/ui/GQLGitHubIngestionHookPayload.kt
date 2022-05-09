package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLGitHubIngestionHookPayload(
    private val gqlEnumIngestionHookPayloadStatus: GQLEnumIngestionHookPayloadStatus,
    private val gqlEnumIngestionEventProcessingResult: GQLEnumIngestionEventProcessingResult,
    private val gqlGitHubRepository: GQLGitHubRepository,
) : GQLType {

    override fun getTypeName(): String = "GitHubIngestionHookPayload"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Payload received by the GitHub Ingestion Hook")
        .stringField(
            IngestionHookPayload::uuid.name,
            getPropertyDescription(IngestionHookPayload::uuid),
        )
        .dateField(
            IngestionHookPayload::timestamp.name,
            getPropertyDescription(IngestionHookPayload::timestamp),
        )
        .stringField(IngestionHookPayload::gitHubDelivery)
        .stringField(IngestionHookPayload::gitHubEvent)
        .intField(IngestionHookPayload::gitHubHookID)
        .intField(IngestionHookPayload::gitHubHookInstallationTargetID)
        .stringField(IngestionHookPayload::gitHubHookInstallationTargetType)
        .field {
            it.name(IngestionHookPayload::payload.name)
                .description(getPropertyDescription(IngestionHookPayload::payload))
                .type(GQLScalarJSON.INSTANCE.toNotNull())
        }
        .field {
            it.name(IngestionHookPayload::status.name)
                .description(getPropertyDescription(IngestionHookPayload::status))
                .type(gqlEnumIngestionHookPayloadStatus.getTypeRef().toNotNull())
        }
        .field {
            it.name(IngestionHookPayload::outcome.name)
                .description(getPropertyDescription(IngestionHookPayload::outcome))
                .type(gqlEnumIngestionEventProcessingResult.getTypeRef())
        }
        .dateField(
            IngestionHookPayload::started.name,
            getPropertyDescription(IngestionHookPayload::started),
        )
        .stringField(IngestionHookPayload::message)
        .dateField(
            IngestionHookPayload::completion.name,
            getPropertyDescription(IngestionHookPayload::completion),
        )
        .field {
            it.name(IngestionHookPayload::repository.name)
                .description(getPropertyDescription(IngestionHookPayload::repository))
                .type(gqlGitHubRepository.typeRef)
        }
        .stringField(IngestionHookPayload::source)
        .stringField(IngestionHookPayload::routing)
        .stringField(IngestionHookPayload::queue)
        .build()
}