import {Button, message, Space} from "antd";
import {useConfirmWithReason} from "@components/common/ConfirmWithReason";
import {useDeploymentCancelAction} from "@components/extension/environments/deployment/steps/deploymentActions";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";

export default function SlotPipelineCancelButton({
                                                     deployment,
                                                     reloadState,
                                                     onCancel,
                                                     showIcon = true,
                                                     showText = false,
                                                     size,
                                                     variant,
                                                     color,
                                                 }) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = async () => {
        messageApi.error(`Could not cancel the deployment`)
    }

    const {action, loading: cancelling} = useDeploymentCancelAction({
        deployment,
        onSuccess: onCancel,
        onError,
    })

    const [cancelConfirm, cancelComponent] = useConfirmWithReason({
        onConfirm: (reason) => action({variables: {reason}}),
        question: "Are you sure you want to cancel this deployment?",
    })

    const {data, loading} = useQuery(
        gql`
            query PipelineActions($id: String!) {
                slotPipelineById(id: $id) {
                    runAction {
                        ok
                    }
                    finishAction {
                        ok
                    }
                }
            }
        `,
        {
            variables: {id: deployment.id},
            deps: [deployment.id, reloadState],
        }
    )

    return (
        <>
            <LoadingInline loading={loading} text="">
                {
                    (data?.slotPipelineById?.runAction !== null || data?.slotPipelineById?.finishAction !== null) &&
                    <Button
                        loading={cancelling}
                        onClick={cancelConfirm}
                        title="Cancels this deployment"
                        size={size}
                        variant={variant}
                        color={color}
                    >
                        <Space>
                            {
                                showIcon &&
                                <SlotPipelineStatusIcon status="CANCELLED"/>
                            }
                            {
                                showText &&
                                "Cancel the deployment"
                            }
                        </Space>
                    </Button>
                }
                {contextHolder}
                {cancelComponent}
            </LoadingInline>
        </>
    )
}