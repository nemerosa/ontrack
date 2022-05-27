package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.paginate
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.*
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionLink
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.IngestionHookPayloadStatus
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.connector.support.PaginatedList
import net.nemerosa.ontrack.kdsl.connector.support.emptyPaginatedList
import java.util.*

/**
 * Management of the GitHub ingestion.
 */
class GitHubIngestionMgt(connector: Connector) : Connected(connector) {

    /**
     * Getting a list of payloads.
     */
    fun payloads(
        offset: Int = 0,
        size: Int = 10,
        uuid: String? = null,
        statuses: List<String>? = null,
        gitHubEvent: String? = null,
        repository: String? = null,
    ): PaginatedList<GitHubIngestionPayload> = graphqlConnector.query(
        GitHubIngestionPayloadsQuery(
            Input.fromNullable(offset),
            Input.fromNullable(size),
            Input.fromNullable(uuid),
            Input.fromNullable(
                statuses?.map {
                    IngestionHookPayloadStatus.valueOf(it)
                }
            ),
            Input.fromNullable(gitHubEvent),
            Input.fromNullable(repository),
        )
    )?.paginate(
        pageInfo = { it.gitHubIngestionHookPayloads()?.pageInfo()?.fragments()?.pageInfoContent() },
        pageItems = { it.gitHubIngestionHookPayloads()?.pageItems() },
    )?.map {
        GitHubIngestionPayload(
            uuid = it.uuid()!!,
            status = it.status().name,
            message = it.message(),
        )
    } ?: emptyPaginatedList()

    /**
     * Sends some validation data for a repository
     */
    fun validateDataByRunId(
        owner: String,
        repository: String,
        runId: Long,
        validation: String,
        validationData: GitHubIngestionValidationDataInput,
        validationStatus: String?,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionValidateDataByRunIdMutation(
                owner,
                repository,
                runId,
                validation,
                net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput.builder()
                    .data(validationData.data)
                    .type(validationData.type)
                    .build(),
                Input.fromNullable(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByRunId()?.fragments()?.payloadUserErrors()?.convert()
        }?.gitHubIngestionValidateDataByRunId()?.payload()?.uuid()?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

    /**
     * Sends some validation data for a repository, using the build name
     */
    fun validateDataByBuildName(
        owner: String,
        repository: String,
        buildName: String,
        validation: String,
        validationData: GitHubIngestionValidationDataInput,
        validationStatus: String?,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionValidateDataByBuildNameMutation(
                owner,
                repository,
                buildName,
                validation,
                net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput.builder()
                    .data(validationData.data)
                    .type(validationData.type)
                    .build(),
                Input.fromNullable(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByBuildName()?.fragments()?.payloadUserErrors()?.convert()
        }?.gitHubIngestionValidateDataByBuildName()?.payload()?.uuid()?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

    /**
     * Sends some validation data for a repository, using the build label
     */
    fun validateDataByBuildLabel(
        owner: String,
        repository: String,
        buildLabel: String,
        validation: String,
        validationData: GitHubIngestionValidationDataInput,
        validationStatus: String?,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionValidateDataByBuildLabelMutation(
                owner,
                repository,
                buildLabel,
                validation,
                net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput.builder()
                    .data(validationData.data)
                    .type(validationData.type)
                    .build(),
                Input.fromNullable(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByBuildLabel()?.fragments()?.payloadUserErrors()?.convert()
        }?.gitHubIngestionValidateDataByBuildLabel()?.payload()?.uuid()?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

    /**
     * Sets some build links
     */
    fun buildLinksByRunId(
        owner: String,
        repository: String,
        runId: Long,
        buildLinks: Map<String, String>,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionBuildLinksByRunIdMutation(
                owner,
                repository,
                runId,
                buildLinks.map { (project, buildRef) ->
                    GitHubIngestionLink.builder()
                        .project(project)
                        .buildRef(buildRef)
                        .build()
                },
            )
        ) {
            it?.gitHubIngestionBuildLinksByRunId()?.fragments()?.payloadUserErrors()?.convert()
        }?.gitHubIngestionBuildLinksByRunId()?.payload()?.uuid()?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

}