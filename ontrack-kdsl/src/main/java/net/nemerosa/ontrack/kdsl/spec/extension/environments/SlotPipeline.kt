package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.StartDeployingPipelineMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotPipelineStatus
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Build
import net.nemerosa.ontrack.kdsl.spec.Resource

class SlotPipeline(
    connector: Connector,
    val id: String,
    val number: Int,
    val slot: Slot,
    val build: Build,
    val status: SlotPipelineStatus,
) : Resource(connector) {

    fun startDeploying() {
        val status = graphqlConnector.mutate(
            StartDeployingPipelineMutation(
                id
            )
        ) { it?.startSlotPipelineDeployment()?.fragments()?.payloadUserErrors()?.convert() }
            ?.startSlotPipelineDeployment()?.deploymentStatus()?.status()
            ?: error("Cannot get the deployment status")
        if (!status) error("Deployment could not be started")
    }

}
