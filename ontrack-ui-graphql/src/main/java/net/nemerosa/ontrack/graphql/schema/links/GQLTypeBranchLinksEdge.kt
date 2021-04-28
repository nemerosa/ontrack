package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getDescription
import net.nemerosa.ontrack.model.links.BranchLinksEdge
import org.springframework.stereotype.Component

@Component
class GQLTypeBranchLinksEdge(
    private val gqlEnumBranchLinksDirection: GQLEnumBranchLinksDirection,
    private val gqlTypeBranchLinksDecoration: GQLTypeBranchLinksDecoration
) : GQLType {

    companion object {
        val TYPENAME: String = BranchLinksEdge::class.java.simpleName
    }

    override fun getTypeName(): String = TYPENAME

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(TYPENAME)
            .description(getDescription(BranchLinksEdge::class))
            .field(BranchLinksEdge::direction, gqlEnumBranchLinksDirection.getTypeRef())
            .field(BranchLinksEdge::linkedTo, GQLTypeBranchLinksNode.TYPENAME)
            // TODO V4 Use listField
            .field {
                it.name(BranchLinksEdge::decorations.name)
                    .description(getDescription(BranchLinksEdge::decorations))
                    .type(
                        GraphQLNonNull(
                            GraphQLList(
                                GraphQLNonNull(
                                    gqlTypeBranchLinksDecoration.typeRef
                                )
                            )
                        )
                    )
            }
            .build()

}