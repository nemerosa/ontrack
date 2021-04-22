package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBranch
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.model.links.BranchLinksNode
import org.springframework.stereotype.Component

@Component
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
            .description(getDescription(BranchLinksNode::class))
            .field(BranchLinksNode::branch, GQLTypeBranch.BRANCH)
            .field(BranchLinksNode::build, GQLTypeBuild.BUILD)
            // TODO V4 Use listField
            .field {
                it.name(BranchLinksNode::edges.name)
                    .description(getDescription(BranchLinksNode::edges))
                    .type(
                        GraphQLNonNull(
                            GraphQLList(
                                GraphQLNonNull(
                                    gqlTypeBranchLinksEdge.typeRef
                                )
                            )
                        )
                    )
            }
            .build()

}