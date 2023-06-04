package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBranch
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.links.BranchLinksNode
import org.springframework.stereotype.Component

@Component
@Deprecated(GraphFieldContributorConstants.DEPRECATION)
class GQLTypeBranchLinksNode(
        private val gqlTypeBranchLinksEdge: GQLTypeBranchLinksEdge
) : GQLType {

    companion object {
        val TYPENAME: String = BranchLinksNode::class.java.simpleName
    }

    override fun getTypeName(): String = TYPENAME

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(TYPENAME)
                    .description(getAPITypeName(BranchLinksNode::class))
                    .field(BranchLinksNode::branch, GQLTypeBranch.BRANCH)
                    .field(BranchLinksNode::build, GQLTypeBuild.BUILD)
                    // TODO V4 Use listField
                    .field {
                        it.name(BranchLinksNode::edges.name)
                                .description(getPropertyDescription(BranchLinksNode::edges))
                                .type(listType(gqlTypeBranchLinksEdge.typeRef))
                    }
                    .build()

}