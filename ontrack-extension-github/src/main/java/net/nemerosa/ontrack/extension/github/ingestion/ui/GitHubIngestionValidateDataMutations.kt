package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByBuildLabelInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByBuildNameInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.GitHubIngestionValidateDataByRunIdInput
import net.nemerosa.ontrack.extension.github.ingestion.validation.IngestionValidateDataService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
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
        unitMutation<GitHubIngestionValidateDataByRunIdInput>(
            name = "gitHubIngestionValidateDataByRunId",
            description = "Sets some validation data on a build identified using a GHA workflow run ID",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
        /**
         * Getting the build by build name
         */
        unitMutation<GitHubIngestionValidateDataByBuildNameInput>(
            name = "gitHubIngestionValidateDataByBuildName",
            description = "Sets some validation data on a build identified using its name",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
        /**
         * Getting the build by build label
         */
        unitMutation<GitHubIngestionValidateDataByBuildLabelInput>(
            name = "gitHubIngestionValidateDataByBuildLabel",
            description = "Sets some validation data on a build identified using its release property (label)",
        ) { input ->
            ingestionValidateDataService.ingestValidationData(input)
        },
    )
}

