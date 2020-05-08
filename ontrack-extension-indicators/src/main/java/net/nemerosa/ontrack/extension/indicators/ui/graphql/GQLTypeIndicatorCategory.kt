package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeIndicatorCategory : GQLType {

    override fun createType(cache: GQLTypeCache) = GraphQLBeanConverter.asObjectType(IndicatorCategory::class.java, cache)

    override fun getTypeName(): String = IndicatorCategory::class.java.simpleName
}