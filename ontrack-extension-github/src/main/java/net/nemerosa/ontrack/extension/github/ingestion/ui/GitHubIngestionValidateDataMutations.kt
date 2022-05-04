package net.nemerosa.ontrack.extension.github.ingestion.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByBuildLabelInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByBuildNameInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByRunIdInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.IngestionValidateDataService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

/**
 * Mutations used to inject data into validations.
 */
@Component
class GitHubIngestionValidateDataMutations(
    private val ingestionValidateDataService: IngestionValidateDataService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Getting the build by run ID
         */
        simpleMutation(
            name = "gitHubIngestionValidateDataByRunId",
            description = "Sets some validation data on a build identified using a GHA workflow run ID",
            input = GitHubIngestionValidateDataByRunIdInput::class,
            outputName = "payload",
            outputType = GitHubIngestionValidateDataOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionValidateDataOutput(
                ingestionValidateDataService.ingestValidationData(input).toString()
            )
        },
        /**
         * Getting the build by build name
         */
        simpleMutation(
            name = "gitHubIngestionValidateDataByBuildName",
            description = "Sets some validation data on a build identified using its name",
            input = GitHubIngestionValidateDataByBuildNameInput::class,
            outputName = "payload",
            outputType = GitHubIngestionValidateDataOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionValidateDataOutput(
                ingestionValidateDataService.ingestValidationData(input).toString()
            )
        },
        /**
         * Getting the build by build label
         */
        simpleMutation(
            name = "gitHubIngestionValidateDataByBuildLabel",
            description = "Sets some validation data on a build identified using its release property (label)",
            input = GitHubIngestionValidateDataByBuildLabelInput::class,
            outputName = "payload",
            outputType = GitHubIngestionValidateDataOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionValidateDataOutput(
                ingestionValidateDataService.ingestValidationData(input).toString()
            )
        },
    )
}

data class GitHubIngestionValidateDataOutput(
    @APIDescription("UUID of the payload being processed in the background")
    val uuid: String,
)

@Component
class GQLTypeGitHubIngestionValidateDataOutput : GQLType {
    override fun getTypeName(): String = GitHubIngestionValidateDataOutput::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(GitHubIngestionValidateDataOutput::class, cache)

}