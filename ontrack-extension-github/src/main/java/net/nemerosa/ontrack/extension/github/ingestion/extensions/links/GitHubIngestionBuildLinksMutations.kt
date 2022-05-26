package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

/**
 * Mutations used to inject build links.
 */
@Component
class GitHubIngestionValidateDataMutations(
    private val ingestionLinksService: IngestionLinksService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Getting the build by run ID
         */
        simpleMutation(
            name = "gitHubIngestionBuildLinksByRunId",
            description = "Sets some links on a build identified using a GHA workflow run ID",
            input = GitHubIngestionLinksByRunIdInput::class,
            outputName = "payload",
            outputType = GitHubIngestionBuildLinksOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionBuildLinksOutput(
                ingestionLinksService.ingestLinks(input).toString()
            )
        },
        /**
         * TODO Getting the build by build name
         */
//        simpleMutation(
//            name = "gitHubIngestionBuildLinksByBuildName",
//            description = "Sets some links on a build identified using its name",
//            input = GitHubIngestionValidateDataByBuildNameInput::class,
//            outputName = "payload",
//            outputType = GitHubIngestionBuildLinksOutput::class,
//            outputDescription = "Payload being processed in the background"
//        ) { input ->
//            GitHubIngestionBuildLinksOutput(
//                TODO()
//            )
//        },
        /**
         * TODO Getting the build by build label
         */
//        simpleMutation(
//            name = "gitHubIngestionBuildLinksByBuildLabel",
//            description = "Sets some links on a build identified using its release property (label)",
//            input = GitHubIngestionValidateDataByBuildLabelInput::class,
//            outputName = "payload",
//            outputType = GitHubIngestionBuildLinksOutput::class,
//            outputDescription = "Payload being processed in the background"
//        ) { input ->
//            GitHubIngestionBuildLinksOutput(
//                TODO()
//            )
//        },
    )
}

data class GitHubIngestionBuildLinksOutput(
    @APIDescription("UUID of the payload being processed in the background")
    val uuid: String,
)
