package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.links.BranchLinksEdge
import org.springframework.stereotype.Component

@Component
@Deprecated(GraphFieldContributorConstants.DEPRECATION)
class GQLTypeBranchLinksEdge(
    private val gqlEnumBranchLinksDirection: GQLEnumBranchLinksDirection,
) : GQLType {

    companion object {
        val TYPENAME: String = BranchLinksEdge::class.java.simpleName
    }

    override fun getTypeName(): String = TYPENAME

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(TYPENAME)
            .description(getAPITypeName(BranchLinksEdge::class))
            .field(BranchLinksEdge::direction, gqlEnumBranchLinksDirection.getTypeRef())
            .field(BranchLinksEdge::linkedTo, GQLTypeBranchLinksNode.TYPENAME)
            .build()

}