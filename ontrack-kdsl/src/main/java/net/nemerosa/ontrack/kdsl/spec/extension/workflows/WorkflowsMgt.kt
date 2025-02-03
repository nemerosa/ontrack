package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.LaunchWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.StopWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.WorkflowInstanceQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.WorkflowInstancesByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.type.LaunchWorkflowInputContext
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.workflows.SaveJsonWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.workflows.SaveYamlWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class WorkflowsMgt(connector: Connector) : Connected(connector) {

    fun saveYamlWorkflow(workflow: String): String? =
        graphqlConnector.mutate(
            SaveYamlWorkflowMutation(workflow)
        ) {
            it?.saveYamlWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.saveYamlWorkflow()?.workflowId()

    fun saveJsonWorkflow(workflow: JsonNode): String? =
        graphqlConnector.mutate(
            SaveJsonWorkflowMutation(workflow)
        ) {
            it?.saveJsonWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.saveJsonWorkflow()?.workflowId()

    fun launchWorkflow(
        workflowId: String,
        context: Map<String, String>,
    ): String? {
        return graphqlConnector.mutate(
            LaunchWorkflowMutation(
                workflowId,
                context.map {
                    LaunchWorkflowInputContext.builder()
                        .name(it.key)
                        .value(it.value)
                        .build()
                }
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

    fun workflowInstancesByName(name: String): List<WorkflowInstance> {
        return graphqlConnector.query(
            WorkflowInstancesByNameQuery(name)
        )?.workflowInstances()?.pageItems()?.map { item ->
            WorkflowInstance(
                status = item.status().run {
                    WorkflowInstanceStatus.valueOf(toString())
                },
                finished = item.finished(),
                nodesExecutions = item.nodesExecutions().map {
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
        } ?: emptyList()
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
