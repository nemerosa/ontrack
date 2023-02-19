package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitChangeLog(
    private val gitChangeLogGQLType: GitChangeLogGQLType,
    private val gitService: GitService,
    private val gitChangeLogCache: GitChangeLogCache,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitChangeLog")
        .argument {
            it.name("from")
                .description("ID of the build to start the change log with")
                .type(GraphQLNonNull(Scalars.GraphQLInt))
        }
        .argument {
            it.name("to")
                .description("ID of the build to end the change log with")
                .type(GraphQLNonNull(Scalars.GraphQLInt))
        }
        .type(gitChangeLogGQLType.typeRef)
        .dataFetcher(gitChangeLogFetcher())
        .build()

    private fun gitChangeLogFetcher() = DataFetcher { environment ->
        val from: Int = environment.getArgument<Int>("from")
        val to: Int = environment.getArgument<Int>("to")
        gitService.changeLog(
            BuildDiffRequest(
                of(from),
                of(to)
            )
        ).apply {
            gitChangeLogCache.put(this)
        }
    }
}