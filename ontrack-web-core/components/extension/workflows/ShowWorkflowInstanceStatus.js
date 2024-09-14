import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";

export default function ShowWorkflowInstanceStatus({instanceId}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [status, setStatus] = useState('')

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query GetWorkflowInstanceStatus($instanceId: String!) {
                        workflowInstance(id: $instanceId) {
                            status
                        }
                    }
                `,
                {instanceId}
            ).then(data => {
                setStatus(data.workflowInstance?.status)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, instanceId])

    return (
        <>
            <LoadingInline loading={loading} text="">
                <WorkflowInstanceStatus status={status}/>
            </LoadingInline>
        </>
    )
}