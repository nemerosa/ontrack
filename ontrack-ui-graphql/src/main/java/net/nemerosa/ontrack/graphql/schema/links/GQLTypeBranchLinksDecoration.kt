package net.nemerosa.ontrack.graphql.schema.links

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypeExtensionFeatureDescription
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.links.BranchLinksDecoration
import org.springframework.stereotype.Component

@Component
class GQLTypeBranchLinksDecoration(
    private val gqlTypeExtensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    override fun getTypeName(): String = BranchLinksDecoration::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("")
            .field(BranchLinksDecoration::feature, gqlTypeExtensionFeatureDescription)
            .stringField(BranchLinksDecoration::id)
            .stringField(BranchLinksDecoration::text)
            .stringField(BranchLinksDecoration::description)
            .stringField(BranchLinksDecoration::icon)
            .stringField(BranchLinksDecoration::url)
            .build()
}