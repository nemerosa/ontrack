package net.nemerosa.ontrack.kdsl.spec.extension.environments

import com.apollographql.apollo.api.Input
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.CreateSlotMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.DeleteEnvironmentMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.Resource

class Environment(
    connector: Connector,
    val id: String,
    val name: String,
    val order: Int,
    val description: String?,
    val tags: List<String> = emptyList(),
) : Resource(connector) {

    fun createSlot(
        project: Project,
        qualifier: String = "",
        description: String = "",
    ): Slot {
        val slot = graphqlConnector.mutate(
            CreateSlotMutation(
                this.id,
                project.id.toInt(),
                qualifier,
                Input.optional(description)
            )
        ) { it?.createSlots()?.fragments()?.payloadUserErrors()?.convert() }
            ?.createSlots()?.slots()?.slots()?.firstOrNull()
            ?: error("Cannot get created slot")
        return Slot(
            connector = connector,
            id = slot.id(),
            environment = this,
            project = project,
            qualifier = qualifier,
            description = description,
        )
    }

    fun delete() {
        graphqlConnector.mutate(
            DeleteEnvironmentMutation(id)
        ) { it?.deleteEnvironment()?.fragments()?.payloadUserErrors()?.convert() }
    }

}
