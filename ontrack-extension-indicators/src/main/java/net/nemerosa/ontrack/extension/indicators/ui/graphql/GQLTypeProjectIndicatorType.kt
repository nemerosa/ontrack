package net.nemerosa.ontrack.extension.indicators.ui.graphql

import net.nemerosa.ontrack.extension.indicators.ui.ProjectIndicatorType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectIndicatorType : GQLType {

    override fun createType(cache: GQLTypeCache) = GraphQLBeanConverter.asObjectType(ProjectIndicatorType::class.java, cache)

    override fun getTypeName(): String = ProjectIndicatorType::class.java.simpleName
}