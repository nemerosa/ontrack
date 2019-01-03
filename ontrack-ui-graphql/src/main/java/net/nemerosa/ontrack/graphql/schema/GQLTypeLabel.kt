package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.labels.Label
import org.springframework.stereotype.Component

@Component
class GQLTypeLabel(
        private val fieldContributors: List<GQLFieldContributor>
) : GQLType {
    override fun getTypeName(): String = Label::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLBeanConverter.asObjectTypeBuilder(Label::class.java, cache, emptySet())
                    // Links
                    .fields(Label::class.java.graphQLFieldContributions(fieldContributors))
                    // OK
                    .build()
}