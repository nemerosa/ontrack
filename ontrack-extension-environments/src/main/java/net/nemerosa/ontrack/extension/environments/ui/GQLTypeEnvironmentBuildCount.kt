package net.nemerosa.ontrack.extension.environments.ui

import graphql.Scalars.GraphQLID
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.promotions.EnvironmentBuildCount
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

@Component
class GQLTypeEnvironmentBuildCount : GQLType {

    override fun getTypeName(): String = EnvironmentBuildCount::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(EnvironmentBuildCount::class))
            .field {
                it.name("id")
                    .description("ID of the count (build ID)")
                    .type(GraphQLID.toNotNull())
                    .dataFetcher { env ->
                        val count = env.getSource<EnvironmentBuildCount>()!!
                        count.build.id()
                    }
            }
            .intField(EnvironmentBuildCount::count)
            .field(EnvironmentBuildCount::build)
            .build()

}