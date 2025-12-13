package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import com.apollographql.apollo.api.Optional
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
            Optional.presentIfNotNull(offset),
            Optional.presentIfNotNull(size),
            Optional.presentIfNotNull(uuid),
            Optional.presentIfNotNull(
                statuses?.map {
                    IngestionHookPayloadStatus.valueOf(it)
                }
            ),
            Optional.presentIfNotNull(gitHubEvent),
            Optional.presentIfNotNull(repository),
        )
    )?.paginate(
        pageInfo = { it.gitHubIngestionHookPayloads?.pageInfo?.pageInfoContent },
        pageItems = { it.gitHubIngestionHookPayloads?.pageItems },
    )?.map {
        GitHubIngestionPayload(
            uuid = it.uuid!!,
            status = it.status.name,
            message = it.message,
            routing = it.routing,
            queue = it.queue,
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
            mutation = GitHubIngestionValidateDataByRunIdMutation(
                owner = owner,
                repository = repository,
                runId = runId,
                validation = validation,
                validationData = net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput(
                    data = validationData.data,
                    type = validationData.type,
                ),
                validationStatus = Optional.presentIfNotNull(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByRunId?.payloadUserErrors?.convert()
        }?.gitHubIngestionValidateDataByRunId?.payload?.uuid?.let { UUID.fromString(it) }
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
                owner = owner,
                repository = repository,
                buildName = buildName,
                validation = validation,
                validationData = net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput(
                    data = validationData.data,
                    type = validationData.type,
                ),
                validationStatus = Optional.presentIfNotNull(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByBuildName?.payloadUserErrors?.convert()
        }?.gitHubIngestionValidateDataByBuildName?.payload?.uuid?.let { UUID.fromString(it) }
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
                owner = owner,
                repository = repository,
                buildLabel = buildLabel,
                validation = validation,
                validationData = net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.GitHubIngestionValidationDataInput(
                    data = validationData.data,
                    type = validationData.type,
                ),
                validationStatus = Optional.presentIfNotNull(validationStatus),
            )
        ) {
            it?.gitHubIngestionValidateDataByBuildLabel?.payloadUserErrors?.convert()
        }
            ?.gitHubIngestionValidateDataByBuildLabel?.payload?.uuid?.let { UUID.fromString(it) }
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
                owner = owner,
                repository = repository,
                runId = runId,
                buildLinks = buildLinks.map { (project, buildRef) ->
                    GitHubIngestionLink(
                        project = project,
                        buildRef = buildRef,
                    )
                },
            )
        ) {
            it?.gitHubIngestionBuildLinksByRunId?.payloadUserErrors?.convert()
        }?.gitHubIngestionBuildLinksByRunId?.payload?.uuid?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

    /**
     * Sets some build links
     */
    fun buildLinksByBuildName(
        owner: String,
        repository: String,
        buildName: String,
        addOnly: Boolean,
        buildLinks: Map<String, String>,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionBuildLinksByBuildNameMutation(
                owner = owner,
                repository = repository,
                buildName = buildName,
                addOnly = addOnly,
                buildLinks = buildLinks.map { (project, buildRef) ->
                    GitHubIngestionLink(
                        project = project,
                        buildRef = buildRef,
                    )
                },
            )
        ) {
            it?.gitHubIngestionBuildLinksByBuildName?.payloadUserErrors?.convert()
        }?.gitHubIngestionBuildLinksByBuildName?.payload?.uuid?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

    /**
     * Sets some build links
     */
    fun buildLinksByBuildLabel(
        owner: String,
        repository: String,
        buildLabel: String,
        buildLinks: Map<String, String>,
    ): UUID =
        graphqlConnector.mutate(
            GitHubIngestionBuildLinksByBuildLabelMutation(
                owner = owner,
                repository = repository,
                buildLabel = buildLabel,
                buildLinks = buildLinks.map { (project, buildRef) ->
                    GitHubIngestionLink(
                        project = project,
                        buildRef = buildRef,
                    )
                },
            )
        ) {
            it?.gitHubIngestionBuildLinksByBuildLabel?.payloadUserErrors?.convert()
        }?.gitHubIngestionBuildLinksByBuildLabel?.payload?.uuid?.let { UUID.fromString(it) }
            ?: error("Could not get the UUID of the processed request")

}