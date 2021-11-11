package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import org.springframework.stereotype.Component

@Component
class GQLGitHubIngestionHookPayload(
    private val gqlEnumIngestionHookPayloadStatus: GQLEnumIngestionHookPayloadStatus,
    private val gqlGitHubRepository: GQLGitHubRepository,
) : GQLType {

    override fun getTypeName(): String = "GitHubIngestionHookPayload"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Payload received by the GitHub Ingestion Hook")
        .stringField(
            IngestionHookPayload::uuid.name,
            getDescription(IngestionHookPayload::uuid),
        )
        .dateField(
            IngestionHookPayload::timestamp.name,
            getDescription(IngestionHookPayload::timestamp),
        )
        .stringField(IngestionHookPayload::gitHubDelivery)
        .stringField(IngestionHookPayload::gitHubEvent)
        .intField(IngestionHookPayload::gitHubHookID)
        .intField(IngestionHookPayload::gitHubHookInstallationTargetID)
        .stringField(IngestionHookPayload::gitHubHookInstallationTargetType)
        .field {
            it.name(IngestionHookPayload::payload.name)
                .description(getDescription(IngestionHookPayload::payload))
                .type(GQLScalarJSON.INSTANCE.toNotNull())
        }
        .field {
            it.name(IngestionHookPayload::status.name)
                .description(getDescription(IngestionHookPayload::status))
                .type(gqlEnumIngestionHookPayloadStatus.getTypeRef().toNotNull())
        }
        .dateField(
            IngestionHookPayload::started.name,
            getDescription(IngestionHookPayload::started),
        )
        .stringField(IngestionHookPayload::message)
        .dateField(
            IngestionHookPayload::completion.name,
            getDescription(IngestionHookPayload::completion),
        )
        .field {
            it.name(IngestionHookPayload::repository.name)
                .description(getDescription(IngestionHookPayload::repository))
                .type(gqlGitHubRepository.typeRef)
        }
        .build()
}