package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource
import org.springframework.stereotype.Component

/**
 * GraphQL type for [BuildFilterResource].
 */
@Component
class GQLTypeBuildFilterResource(
        private val fieldContributors: List<GQLFieldContributor>,
) : GQLType {
    override fun getTypeName(): String = BuildFilterResource::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Shared build filter for a branch.")
            .booleanField(BuildFilterResource<Any>::isShared)
            .stringField(BuildFilterResource<Any>::type)
            .stringField(BuildFilterResource<Any>::name)
            .stringField(BuildFilterResource<Any>::error)
            .jsonField(BuildFilterResource<Any>::data)
            // Links
            .fields(BuildFilterResource::class.java.graphQLFieldContributions(fieldContributors))
            // OK
            .build()
}