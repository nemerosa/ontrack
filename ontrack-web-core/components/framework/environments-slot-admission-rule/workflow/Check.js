import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import LoadingInline from "@components/common/LoadingInline";
import {useEffect, useState} from "react";
import {Typography} from "antd";
import {gql} from "graphql-request";
import WorkflowInstanceLink from "@components/extension/workflows/WorkflowInstanceLink";

export default function WorkflowAdmissionRuleCheck({check, ruleConfig, ruleData}) {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [slotWorkflowInstance, setSlotWorkflowInstance] = useState(null)
    useEffect(() => {
        if (client) {
            setLoading(true)
            if (ruleData?.slotWorkflowInstanceId) {
                client.request(
                    gql`
                        query SlotWorkflowInstance($slotWorkflowInstanceId: String!) {
                            slotWorkflowInstanceById(id: $slotWorkflowInstanceId) {
                                workflowInstance {
                                    id
                                    status
                                    workflow {
                                        name
                                    }
                                }
                            }
                        }
                    `,
                    {
                        slotWorkflowInstanceId: ruleData.slotWorkflowInstanceId,
                    }
                ).then(data => {
                    setSlotWorkflowInstance(data.slotWorkflowInstanceById)
                }).finally(() => {
                    setLoading(false)
                })
            } else {
                setSlotWorkflowInstance(null)
                setLoading(false)
            }
        }
    }, [client, ruleData])

    return (
        <>
            <LoadingInline loading={loading} text="Loading workflow details...">
                {
                    !slotWorkflowInstance && <Typography.Text type="secondary">No workflow found</Typography.Text>
                }
                {
                    slotWorkflowInstance && <WorkflowInstanceLink
                        workflowInstanceId={slotWorkflowInstance.workflowInstance.id}
                        name={slotWorkflowInstance.workflowInstance.workflow.name}
                        status={slotWorkflowInstance.workflowInstance.status}
                    />
                }
            </LoadingInline>
        </>
    )
}