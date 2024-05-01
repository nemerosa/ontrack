package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.LaunchWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.SaveYamlWorkflowMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.WorkflowInstanceQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector

class WorkflowsMgt(connector: Connector) : Connected(connector) {

    fun saveYamlWorkflow(workflow: String, executor: String): String? =
        graphqlConnector.mutate(
            SaveYamlWorkflowMutation(workflow, executor)
        ) {
            it?.saveYamlWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.saveYamlWorkflow()?.workflowId()

    fun launchWorkflow(workflowId: String): String? =
        graphqlConnector.mutate(
            LaunchWorkflowMutation(workflowId)
        ) {
            it?.launchWorkflow()?.fragments()?.payloadUserErrors()?.convert()
        }?.launchWorkflow()?.workflowInstanceId()

    fun workflowInstance(instanceId: String): WorkflowInstance? {
        return graphqlConnector.query(
            WorkflowInstanceQuery(instanceId)
        )?.workflowInstance()?.run {
            WorkflowInstance(
                status = status().run {
                    WorkflowInstanceStatus.valueOf(toString())
                },
                finished = finished(),
            )
        }
    }

}
