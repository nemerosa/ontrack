package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import org.springframework.stereotype.Component

@Component
class GQLTypeBuildPromotionInfo : GQLType {
    override fun getTypeName(): String = BuildPromotionInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about the promotions of a build")
            .field {
                it.name(BuildPromotionInfo::noPromotionItems.name)
                    .description("Items not linked to any promotion")
                    .type(listType(BuildPromotionInfoItem::class.java.simpleName))
                    .dataFetcher { env ->
                        val buildPromotionInfo: BuildPromotionInfo = env.getSource()
                        buildPromotionInfo.noPromotionItems.map { item -> item.data }
                    }
            }
            .listField(BuildPromotionInfo::withPromotionItems)
            .build()
}