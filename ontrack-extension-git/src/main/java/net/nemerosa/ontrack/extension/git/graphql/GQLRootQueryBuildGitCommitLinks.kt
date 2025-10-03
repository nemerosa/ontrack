package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.git.service.BuildGitCommitLinkService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLRootQueryBuildGitCommitLinks(
    private val gqlTypeBuildGitCommitLink: GQLTypeBuildGitCommitLink,
    private val buildGitCommitLinkService: BuildGitCommitLinkService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("buildGitCommitLinks")
            .description("List of build/commit links")
            .type(listType(gqlTypeBuildGitCommitLink.typeRef))
            .dataFetcher {
                buildGitCommitLinkService.links.sortedBy { it.id }
            }
            .build()

}