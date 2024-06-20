package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.LaunchWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SaveYamlWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.StopWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.WorkflowInstanceQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.LaunchWorkflowInputContext
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class WorkflowsMgt(connector: Connector) : Connected(connector) {

    fun saveYamlWorkflow(workflow: String): String? =
        graphqlConnector.mutate(
            SaveYamlWorkflowMutation(workflow)
        ) {
            it?.saveYamlWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.saveYamlWorkflow()?.workflowId()

    fun launchWorkflow(workflowId: String, context: Pair<String, JsonNode>): String? {
        val (key, value) = context
        return graphqlConnector.mutate(
            LaunchWorkflowMutation(
                workflowId, listOf(
                    LaunchWorkflowInputContext.builder()
                        .key(key)
                        .value(value)
                        .build()
                )
            )
        ) {
            it?.launchWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.launchWorkflow()?.workflowInstanceId()
    }

    fun stopWorkflow(instanceId: String) {
        graphqlConnector.mutate(
            StopWorkflowMutation(instanceId)
        ) {
            it?.stopWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }
    }

    fun workflowInstance(instanceId: String): WorkflowInstance? {
        return graphqlConnector.query(
            WorkflowInstanceQuery(instanceId)
        )?.workflowInstance()?.run {
            WorkflowInstance(
                status = status().run {
                    WorkflowInstanceStatus.valueOf(toString())
                },
                finished = finished(),
                nodesExecutions = nodesExecutions().map {
                    WorkflowInstanceNode(
                        id = it.id(),
                        status = it.status().run {
                            WorkflowInstanceNodeStatus.valueOf(toString())
                        },
                        output = it.output(),
                        error = it.error(),
                    )
                }
            )
        }
    }

}
