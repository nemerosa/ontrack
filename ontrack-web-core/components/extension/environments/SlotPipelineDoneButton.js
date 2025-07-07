import {message} from "antd";
import {gql} from "graphql-request";
import {useDeploymentFinishAction} from "@components/extension/environments/deployment/steps/deploymentActions";
import {useQuery} from "@components/services/useQuery";
import SlotPipelineActionButton from "@components/extension/environments/SlotPipelineActionButton";

export default function SlotPipelineDoneButton({
                                                   pipeline,
                                                   reloadState,
                                                   onFinish,
                                                   size,
                                                   variant,
                                                   color,
                                                   showDisabledButtonIfNotOk = false,
                                                   showIcon = true,
                                                   showText = false,
                                               }) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = () => {
        messageApi.error("Could not complete the deployment")
    }

    const {action, loading: finishing} = useDeploymentFinishAction({
        deployment: pipeline,
        onSuccess: onFinish,
        onError: onError,
    })

    const {data, loading} = useQuery(
        gql`
            query PipelineFinishAction($id: String!) {
                slotPipelineById(id: $id) {
                    finishAction {
                        ok
                    }
                }
            }
        `,
        {
            variables: {id: pipeline.id},
            deps: [pipeline.id, reloadState],
            dataFn: (data) => data.slotPipelineById?.finishAction,
        }
    )

    return (
        <>
            {contextHolder}
            <SlotPipelineActionButton
                id={`pipeline-finish-${pipeline.id}`}
                status="DONE"
                actionStateData={data}
                actionStateLoading={loading}
                confirmTitle="Deployment done"
                confirmDescription="This will mark this deployment as being done. Do you want to continue?"
                buttonTitle="Marks this deployment as done"
                buttonText="Finish the deployment"
                action={action}
                actionRunning={finishing}
                size={size}
                variant={variant}
                color={color}
                showDisabledButtonIfNotOk={showDisabledButtonIfNotOk}
                showIcon={showIcon}
                showText={showText}
            />
        </>
    )
}