import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import SlotWorkflowDialog, {useSlotWorkflowDialog} from "@components/extension/environments/SlotWorkflowDialog";
import {Button, Space, Table} from "antd";
import {isAuthorized} from "@components/common/authorizations";
import {FaPlus} from "react-icons/fa";
import SlotWorkflowTrigger from "@components/extension/environments/SlotWorkflowTrigger";
import SlotWorkflowEditButton from "@components/extension/environments/SlotWorkflowEditButton";
import SlotWorkflowDeleteButton from "@components/extension/environments/SlotWorkflowDeleteButton";
import ShowWorkflowButton from "@components/extension/workflows/ShowWorkflowButton";

export default function SlotWorkflowsTable({slot, onChange}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [workflows, setWorkflows] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query SlotWorkflows($id: String!) {
                        slotById(id: $id) {
                            workflows {
                                id
                                trigger
                                workflow {
                                    name
                                    nodes {
                                        id
                                        executorId
                                        timeout
                                        data
                                        parents {
                                            id
                                        }
                                    }
                                }
                            }
                        }
                    }
                `,
                {id: slot.id}
            ).then(data => {
                setWorkflows(data.slotById.workflows)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, slot])

    const dialog = useSlotWorkflowDialog({
        onSuccess: onChange,
    })

    const addWorkflow = async () => {
        dialog.start({slot})
    }

    return (
        <>
            <SlotWorkflowDialog dialog={dialog}/>
            <Table
                dataSource={workflows}
                loading={loading}
                pagination={false}
                size="small"
                footer={() =>
                    <Space>
                        {
                            isAuthorized(slot, "slot", "edit") &&
                            <Button
                                icon={<FaPlus/>}
                                onClick={addWorkflow}
                            >
                                Add workflow
                            </Button>
                        }
                    </Space>
                }
            >
                <Table.Column
                    key="trigger"
                    title="Trigger"
                    render={(_, slotWorkflow) => <SlotWorkflowTrigger trigger={slotWorkflow.trigger}/>}
                />
                <Table.Column
                    key="name"
                    title="Name"
                    render={(_, slotWorkflow) => <ShowWorkflowButton workflow={slotWorkflow.workflow}/>}
                />
                <Table.Column
                    key="actions"
                    title="Actions"
                    render={(_, slotWorkflow) => <Space>
                        {
                            isAuthorized(slot, "slot", "edit") &&
                            <SlotWorkflowEditButton slot={slot} slotWorkflow={slotWorkflow} onChange={onChange}/>
                        }
                        {
                            isAuthorized(slot, "slot", "edit") &&
                            <SlotWorkflowDeleteButton
                                slot={slot}
                                slotWorkflow={slotWorkflow}
                                onChange={onChange}
                            />
                        }
                    </Space>}
                />
            </Table>
        </>
    )
}