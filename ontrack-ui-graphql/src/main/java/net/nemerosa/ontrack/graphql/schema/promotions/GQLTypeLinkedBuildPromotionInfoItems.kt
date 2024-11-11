package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.promotions.LinkedBuildPromotionInfoItems
import org.springframework.stereotype.Component

@Component
class GQLTypeLinkedBuildPromotionInfoItems : GQLType {

    override fun getTypeName(): String = LinkedBuildPromotionInfoItems::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information linked to promotion levels")
            .field(LinkedBuildPromotionInfoItems::promotionLevel)
            .field {
                it.name(LinkedBuildPromotionInfoItems::items.name)
                    .description("Items linked to this promotion")
                    .type(listType(BuildPromotionInfoItem::class.java.simpleName))
                    .dataFetcher { env ->
                        val linkedBuildPromotionInfoItems: LinkedBuildPromotionInfoItems = env.getSource()
                        linkedBuildPromotionInfoItems.items.map { item -> item.data }
                    }
            }
            .build()
}