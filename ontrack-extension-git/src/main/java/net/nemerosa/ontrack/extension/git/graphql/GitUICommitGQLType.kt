package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitUICommit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObjectType
import org.springframework.stereotype.Component

/**
 * @see net.nemerosa.ontrack.extension.git.model.GitUICommit
 */
@Component
class GitUICommitGQLType : GQLType {

    override fun getTypeName(): String = GitUICommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        asObjectType(GitUICommit::class, cache)

}