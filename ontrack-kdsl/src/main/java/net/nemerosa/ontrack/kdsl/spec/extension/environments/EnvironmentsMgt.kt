package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.CreateEnvironmentMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.FindEnvironmentByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.FindPipelineByIdQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class EnvironmentsMgt(connector: Connector) : Connected(connector) {

    fun createEnvironment(
        name: String,
        order: Int,
        description: String = "",
        tags: List<String> = emptyList(),
    ): Environment {
        val environment = graphqlConnector.mutate(
            CreateEnvironmentMutation(
                name,
                order,
                description,
                tags,
            )
        ) { it?.createEnvironment()?.fragments()?.payloadUserErrors()?.convert() }
            ?.createEnvironment()?.environment()
            ?: error("Cannot get created environment")
        return Environment(
            connector = connector,
            id = environment.id(),
            name = name,
            order = order,
            description = description,
        )
    }

    fun findEnvironmentByName(name: String): Environment? =
        graphqlConnector.query(
            FindEnvironmentByNameQuery(name)
        )?.environmentByName()?.fragments()
            ?.environmentFragment()?.toEnvironment(this)

    fun findPipelineById(id: String): SlotPipeline? =
        graphqlConnector.query(
            FindPipelineByIdQuery(id)
        )?.slotPipelineById()?.fragments()
            ?.slotPipelineFragment()?.toPipeline(this)

}