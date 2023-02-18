package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.extension.git.model.GitUICommit
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObjectType
import org.junit.jupiter.api.Test

class GitUICommitGQLTypeTest {

    @Test
    fun `GraphQL type`() {
        asObjectType(GitUICommit::class, GQLTypeCache())
    }

}