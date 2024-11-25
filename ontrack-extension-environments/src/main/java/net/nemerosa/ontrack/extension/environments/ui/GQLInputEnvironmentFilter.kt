package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.environments.EnvironmentFilter
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLInputEnvironmentFilter : GQLInputType<EnvironmentFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(EnvironmentFilter::class, dictionary)

    override fun convert(argument: Any?): EnvironmentFilter? =
        argument?.asJson()?.parse()

    override fun getTypeRef() = GraphQLTypeReference(
        EnvironmentFilter::class.java.simpleName
    )
}