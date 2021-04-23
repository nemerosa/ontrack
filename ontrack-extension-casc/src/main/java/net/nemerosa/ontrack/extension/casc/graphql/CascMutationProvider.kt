package net.nemerosa.ontrack.extension.casc.graphql

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import net.nemerosa.ontrack.extension.casc.CascLoadingService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.schema.MutationProvider
import org.springframework.stereotype.Component

@Component
class CascMutationProvider(
    private val cascLoadingService: CascLoadingService,
) : MutationProvider {

    override val mutations: List<Mutation> = listOf(
        object : Mutation {

            override val name: String = "reloadCasc"
            override val description: String = "Reload the configuration from the CasC files"
            override val inputFields: List<GraphQLInputObjectField> = emptyList()

            override val outputFields: List<GraphQLFieldDefinition> = emptyList()

            override fun fetch(env: DataFetchingEnvironment): Any {
                cascLoadingService.load()
                return ""
            }

        }
    )
}