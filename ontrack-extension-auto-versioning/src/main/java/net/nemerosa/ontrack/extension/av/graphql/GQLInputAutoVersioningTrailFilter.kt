package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTrailFilter
import net.nemerosa.ontrack.graphql.schema.GQLInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class GQLInputAutoVersioningTrailFilter : GQLInputType<AutoVersioningTrailFilter> {

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType =
        GraphQLBeanConverter.asInputType(
            type = AutoVersioningTrailFilter::class,
            dictionary = dictionary,
        )

    override fun convert(argument: Any?): AutoVersioningTrailFilter =
        argument?.asJson()?.parse() ?: AutoVersioningTrailFilter()

    override fun getTypeRef() = GraphQLTypeReference(AutoVersioningTrailFilter::class.java.simpleName)
}