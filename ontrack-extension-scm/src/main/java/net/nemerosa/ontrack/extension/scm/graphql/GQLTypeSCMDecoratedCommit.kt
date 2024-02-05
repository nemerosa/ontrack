package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.changelog.SCMDecoratedCommit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMDecoratedCommit(
    private val gqlTypeSCMCommit: GQLTypeSCMCommit,
) : GQLType {

    override fun getTypeName(): String = SCMDecoratedCommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(SCMDecoratedCommit::class))
            .field(SCMDecoratedCommit::commit, gqlTypeSCMCommit)
            .build()
}
