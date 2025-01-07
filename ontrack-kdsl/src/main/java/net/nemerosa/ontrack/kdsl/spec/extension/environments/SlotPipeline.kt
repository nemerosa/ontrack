package net.nemerosa.ontrack.kdsl.spec.extension.environments

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.FinishDeploymentPipelineMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.PipelineRequiredInputsQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.StartDeployingPipelineMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.environments.UpdatePipelineDataMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.SlotPipelineDataInputValue
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

    fun startDeploying(): SlotPipeline {
        val status = graphqlConnector.mutate(
            StartDeployingPipelineMutation(
                id
            )
        ) { it?.startSlotPipelineDeployment()?.fragments()?.payloadUserErrors()?.convert() }
            ?.startSlotPipelineDeployment()?.deploymentStatus()?.ok()
            ?: error("Cannot get the deployment status")
        if (!status) error("Deployment could not be started")
        return this
    }

    fun finishDeployment(): SlotPipeline {
        graphqlConnector.mutate(
            FinishDeploymentPipelineMutation(
                id
            )
        ) { it?.finishSlotPipelineDeployment()?.fragments()?.payloadUserErrors()?.convert() }
        return this
    }

    fun manualApproval(message: String): SlotPipeline {
        // Getting the inputs of this pipeline
        val configId = graphqlConnector.query(
            PipelineRequiredInputsQuery(id)
        )?.slotPipelineById()?.requiredInputs()?.find {
            it.config().ruleId() == "manual"
        }?.config()?.id() ?: error("Could not find any manual rule")
        // Sending the message
        graphqlConnector.mutate(
            UpdatePipelineDataMutation(
                id,
                listOf(
                    SlotPipelineDataInputValue.builder()
                        .configId(configId)
                        .data(
                            mapOf(
                                "approval" to true,
                                "message" to message
                            ).asJson()
                        )
                        .build()
                )
            )
        ) { it?.updatePipelineData()?.fragments()?.payloadUserErrors()?.convert() }
        // OK
        return this
    }

}
