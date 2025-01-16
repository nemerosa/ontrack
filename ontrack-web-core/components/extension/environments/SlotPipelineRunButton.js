import {message} from "antd";
import {gql} from "graphql-request";
import {useDeploymentRunAction} from "@components/extension/environments/deployment/steps/deploymentActions";
import {useQuery} from "@components/services/useQuery";
import SlotPipelineActionButton from "@components/extension/environments/SlotPipelineActionButton";

export default function SlotPipelineRunButton({
                                                  pipeline,
                                                  reloadState,
                                                  onDeploy,
                                                  size,
                                                  showDisabledButtonIfNotOk = false,
                                                  showIcon = true,
                                                  showText = false,
                                              }) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = async () => {
        messageApi.error("Could not start the deployment")
    }

    const {action, loading: running} = useDeploymentRunAction({
        deployment: pipeline,
        onSuccess: onDeploy,
        onError: onError,
    })

    const {data, loading} = useQuery(
        gql`
            query PipelineRunAction($id: String!) {
                slotPipelineById(id: $id) {
                    runAction {
                        ok
                    }
                }
            }
        `,
        {
            variables: {id: pipeline.id},
            deps: [pipeline.id, reloadState],
            dataFn: (data) => data.slotPipelineById?.runAction,
        }
    )

    return (
        <>
            {contextHolder}
            <SlotPipelineActionButton
                id={`pipeline-deploy-${pipeline.id}`}
                status="RUNNING"
                actionStateData={data}
                actionStateLoading={loading}
                confirmTitle="Running deployment"
                confirmDescription="Running this deployment may affect some running services. Do you want to continue?"
                buttonTitle="Starts this deployment"
                buttonText="Start the deployment"
                action={action}
                actionRunning={running}
                size={size}
                showDisabledButtonIfNotOk={showDisabledButtonIfNotOk}
                showIcon={showIcon}
                showText={showText}
            />
        </>
    )
}