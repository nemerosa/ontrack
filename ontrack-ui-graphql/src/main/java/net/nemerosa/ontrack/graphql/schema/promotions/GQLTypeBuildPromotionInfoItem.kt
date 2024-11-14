package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildPromotionInfoItem : GQLType {
    override fun getTypeName(): String = BuildPromotionInfoItem::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about the promotions of a build")
            .field(BuildPromotionInfoItem<*>::promotionLevel)
            .field {
                it.name(BuildPromotionInfoItem<*>::data.name)
                    .description("Item's data")
                    .type(GraphQLTypeReference(GQLUnionBuildPromotionInfoItemData.TYPE).toNotNull())
            }
            .build()
}