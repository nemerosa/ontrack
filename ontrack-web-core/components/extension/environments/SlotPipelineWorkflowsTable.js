import {gql} from "graphql-request";
import {Table, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import SlotWorkflowTrigger from "@components/extension/environments/SlotWorkflowTrigger";
import TimestampText from "@components/common/TimestampText";
import WorkflowInstanceLink from "@components/extension/workflows/WorkflowInstanceLink";

export default function SlotPipelineWorkflowsTable({pipeline}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [instances, setInstances] = useState([])
    useEffect(() => {
        if (client && pipeline.id) {
            setLoading(true)
            client.request(
                gql`
                    query PipelineWorkflows($id: String!) {
                        slotPipelineById(id: $id) {
                            slotWorkflowInstances {
                                id
                                start
                                slotWorkflow {
                                    id
                                    trigger
                                    workflow {
                                        name
                                    }
                                }
                                workflowInstance {
                                    id
                                    status
                                }
                            }
                        }
                    }
                `,
                {id: pipeline.id}
            ).then(data => {
                setInstances(data.slotPipelineById.slotWorkflowInstances)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pipeline.id])

    return (
        <>
            <Table
                id={`pipeline-workflows-table-${pipeline.id}`}
                data-testid={`pipeline-workflows-table-${pipeline.id}`}
                loading={loading}
                dataSource={instances}
            >
                <Table.Column
                    key="trigger"
                    title="Trigger"
                    render={(_, instance) =>
                        <span data-testid={`pipeline-workflow-${instance.slotWorkflow.id}-trigger`}>
                            <SlotWorkflowTrigger trigger={instance.slotWorkflow.trigger}/>
                        </span>
                    }
                />
                <Table.Column
                    key="workflowName"
                    title="Workflow name"
                    render={(_, instance) => <Typography.Text>{instance.slotWorkflow.workflow.name}</Typography.Text>}
                />
                <Table.Column
                    key="start"
                    title="Start"
                    render={(_, instance) => <TimestampText value={instance.start}/>}
                />
                <Table.Column
                    key="status"
                    title="Status"
                    render={(_, instance) =>
                        instance.workflowInstance?.id ?
                            <span data-testid={`pipeline-workflow-${instance.slotWorkflow.id}-status`}>
                                <WorkflowInstanceLink
                                    workflowInstanceId={instance.workflowInstance.id}
                                    status={instance.workflowInstance.status}
                                    name={instance.slotWorkflow.workflow.name}
                                />
                            </span> : <Typography.Text type="secondary">Not started</Typography.Text>
                    }
                />
            </Table>
        </>
    )
}