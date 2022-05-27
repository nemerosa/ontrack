package net.nemerosa.ontrack.extension.github.ingestion.extensions.links

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

/**
 * Mutations used to inject build links.
 */
@Component
class GitHubIngestionBuildLinksMutations(
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
         * Getting the build by build name
         */
        simpleMutation(
            name = "gitHubIngestionBuildLinksByBuildName",
            description = "Sets some links on a build identified using its name",
            input = GitHubIngestionLinksByBuildNameInput::class,
            outputName = "payload",
            outputType = GitHubIngestionBuildLinksOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionBuildLinksOutput(
                ingestionLinksService.ingestLinks(input).toString()
            )
        },
        /**
         * Getting the build by build label
         */
        simpleMutation(
            name = "gitHubIngestionBuildLinksByBuildLabel",
            description = "Sets some links on a build identified using its release property (label)",
            input = GitHubIngestionLinksByBuildLabelInput::class,
            outputName = "payload",
            outputType = GitHubIngestionBuildLinksOutput::class,
            outputDescription = "Payload being processed in the background"
        ) { input ->
            GitHubIngestionBuildLinksOutput(
                ingestionLinksService.ingestLinks(input).toString()
            )
        },
    )
}

data class GitHubIngestionBuildLinksOutput(
    @APIDescription("UUID of the payload being processed in the background")
    val uuid: String,
)

@Component
class GQLTypeGitHubIngestionBuildLinksOutput : GQLType {
    override fun getTypeName(): String = GitHubIngestionBuildLinksOutput::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(GitHubIngestionBuildLinksOutput::class, cache)

}
