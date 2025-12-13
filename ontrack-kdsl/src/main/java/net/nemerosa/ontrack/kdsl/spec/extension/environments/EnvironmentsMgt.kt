package net.nemerosa.ontrack.kdsl.spec.extension.environments

import com.apollographql.apollo.api.Optional
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.*
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class EnvironmentsMgt(connector: Connector) : Connected(connector) {

    fun list(): List<Environment> =
        graphqlConnector.query(
            ListEnvironmentsQuery()
        )?.environments?.map {
            it.environmentFragment.toEnvironment(this)
        } ?: emptyList()

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
                Optional.presentIfNotNull(description),
                Optional.presentIfNotNull(tags),
            )
        ) { it?.createEnvironment?.payloadUserErrors?.convert() }
            ?.createEnvironment?.environment
            ?: error("Cannot get created environment")
        return Environment(
            connector = connector,
            id = environment.id,
            name = name,
            order = order,
            description = description,
        )
    }

    fun findEnvironmentByName(name: String): Environment? =
        graphqlConnector.query(
            FindEnvironmentByNameQuery(name)
        )?.environmentByName
            ?.environmentFragment?.toEnvironment(this)

    fun findPipelineById(id: String): SlotPipeline? =
        graphqlConnector.query(
            FindPipelineByIdQuery(id)
        )?.slotPipelineById
            ?.slotPipelineFragment?.toPipeline(this)

    fun findSlot(environment: String, project: String): Slot? =
        graphqlConnector.query(
            FindSlotQuery(environment, project)
        )?.environmentByName
            ?.slots?.firstOrNull()
            ?.slotFragment?.toSlot(this)

    fun getSlot(environment: String, project: String): Slot =
        findSlot(environment, project)
            ?: error("Could not find slot")

}