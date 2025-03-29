package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.git.model.GitUICommit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObjectType
import org.springframework.stereotype.Component

@Component
class GQLTypeGitUICommit(
    private val recursiveChangeLogService: RecursiveChangeLogService,
) : GQLType {

    override fun getTypeName(): String = GitUICommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        asObjectType(GitUICommit::class, cache) {
            field {
                it.name("build")
                    .description("Build associated with this commit")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val gitUICommit: GitUICommit = env.getSource()!!
                        recursiveChangeLogService.getBuildByCommit(gitUICommit.id)
                    }
            }
        }

}