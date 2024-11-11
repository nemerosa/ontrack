package net.nemerosa.ontrack.graphql.schema.promotions

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import net.nemerosa.ontrack.extension.api.BuildPromotionInfoExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.graphql.schema.GQLContributor
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GQLContributorBuildPromotionInfo(
    private val extensionManager: ExtensionManager,
) : GQLContributor {

    private val extensions: Collection<BuildPromotionInfoExtension> by lazy {
        extensionManager.getExtensions(BuildPromotionInfoExtension::class.java)
    }

    override fun contribute(cache: GQLTypeCache, dictionary: MutableSet<GraphQLType>): Set<GraphQLType> {

        val buildPromotionInfoItemTypes = mutableSetOf<KClass<*>>()
        buildPromotionInfoItemTypes += PromotionLevel::class
        buildPromotionInfoItemTypes += PromotionRun::class
        buildPromotionInfoItemTypes += extensions.flatMap { it.types }

        val gqlUnionBuildPromotionInfoItem = GraphQLUnionType.newUnionType()
            .name(BuildPromotionInfoItem::class.java.simpleName)
            .description("Information about the promotion of a build")
            .possibleTypes(
                *buildPromotionInfoItemTypes.map {
                    GraphQLTypeReference(it.java.simpleName)
                }.toTypedArray()
            )
            .typeResolver { env ->
                val o = env.getObject<Any>()
                val oName = o::class.java.simpleName
                dictionary.find { it is GraphQLObjectType && it.name == oName } as? GraphQLObjectType?
            }
            .build()

        return setOf(
            gqlUnionBuildPromotionInfoItem,
        )
    }

}