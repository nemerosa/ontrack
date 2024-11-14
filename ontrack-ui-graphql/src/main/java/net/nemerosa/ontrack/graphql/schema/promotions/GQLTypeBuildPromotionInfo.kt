package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildPromotionInfo : GQLType {
    override fun getTypeName(): String = BuildPromotionInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about the promotions of a build")
            .listField(BuildPromotionInfo::items)
            .build()
}