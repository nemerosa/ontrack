import {FaThumbsUp} from "react-icons/fa";
import {Button, message, Popconfirm} from "antd";
import {useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {getUserErrors} from "@components/services/graphql-utils";

export default function SlotPipelineFinishButton({pipeline, onFinish, size}) {
    const [messageApi, contextHolder] = message.useMessage()
    const client = useGraphQLClient()
    const [changing, setChanging] = useState(false)

    const deployed = async () => {
        setChanging(true)
        try {
            const data = await client.request(
                gql`
                    mutation FinishDeployment($id: String!) {
                        finishSlotPipelineDeployment(input: {
                            pipelineId: $id,
                            forcing: false,
                            message: null,
                        }) {
                            finishStatus {
                                deployed
                                message
                            }
                            errors {
                                message
                            }
                        }
                    }
                `,
                {id: pipeline.id}
            )
            // Errors
            const errors = getUserErrors(data.finishSlotPipelineDeployment)
            if (errors) {
                messageApi.error(
                    `Error triggering the completion of the deployment: ${errors}`
                )
            } else {
                // Status
                const finishStatus = data.finishSlotPipelineDeployment.finishStatus
                if (finishStatus) {
                    if (finishStatus.deployed) {
                        messageApi.success("Deployment finished")
                        if (onFinish) onFinish()
                    } else {
                        messageApi.success(`Deployment completion failed: ${finishStatus.message}`)
                    }
                } else {
                    messageApi.error(
                        "Did not receive any completion status"
                    )
                }
            }
        } finally {
            setChanging(false)
        }
    }

    return (
        <>
            {contextHolder}
            {
                pipeline.status === 'DEPLOYING' &&
                <Popconfirm
                    title="Deployed pipeline"
                    description="This will mark this build as being deployed. Do you want to continue?"
                    onConfirm={deployed}
                >
                    <Button
                        icon={<FaThumbsUp color="green"/>}
                        title="Marks this pipeline as deployed"
                        loading={changing}
                        data-testid={`pipeline-finish-${pipeline.id}`}
                        size={size}
                    />
                </Popconfirm>
            }
        </>
    )
}