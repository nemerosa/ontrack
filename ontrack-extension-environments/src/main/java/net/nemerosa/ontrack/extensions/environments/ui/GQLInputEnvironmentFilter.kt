package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extensions.environments.EnvironmentFilter
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObject
import org.springframework.stereotype.Component

@Component
class GQLInputEnvironmentFilter : GQLInputType<EnvironmentFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(EnvironmentFilter::class, dictionary)

    override fun convert(argument: Any?): EnvironmentFilter? =
        asObject(
            argument,
            EnvironmentFilter::class.java
        )

    override fun getTypeRef() = GraphQLTypeReference(
        EnvironmentFilter::class.java.simpleName
    )
}