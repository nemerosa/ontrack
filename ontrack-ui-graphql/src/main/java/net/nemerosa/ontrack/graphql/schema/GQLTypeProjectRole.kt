package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.ProjectRole
import org.springframework.stereotype.Component

/**
 * @see ProjectRole
 */
@Component
class GQLTypeProjectRole : GQLType {
    override fun getTypeName(): String = PROJECT_ROLE

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(PROJECT_ROLE)
                    .stringField(ProjectRole::id)
                    .stringField(ProjectRole::name)
                    .stringField(ProjectRole::description)
                    .build()

    companion object {
        const val PROJECT_ROLE = "ProjectRole"
    }
}
