package net.nemerosa.ontrack.extension.git.graphql

import graphql.Scalars
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import net.nemerosa.ontrack.extension.git.GitChangeLogCache
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import org.springframework.stereotype.Component

@Component
class GQLRootQueryGitChangeLogByUUID(
    private val gitChangeLogGQLType: GitChangeLogGQLType,
    private val gitChangeLogCache: GitChangeLogCache,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("gitChangeLogByUUID")
        .description("Gets a change log by its UUID")
        .argument {
            it.name("uuid")
                .description("UUID of the change log")
                .type(GraphQLNonNull(Scalars.GraphQLString))
        }
        .type(gitChangeLogGQLType.typeRef)
        .dataFetcher(gitChangeLogFetcher())
        .build()

    private fun gitChangeLogFetcher() = DataFetcher { environment ->
        val uuid: String = environment.getArgument("uuid")
        gitChangeLogCache.getRequired(uuid)
    }
}