import {Button, message, Popconfirm, Progress, Space} from "antd";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {
    useDeploymentCancelAction,
    useDeploymentFinishAction,
    useDeploymentRunAction
} from "@components/extension/environments/deployment/steps/deploymentActions";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";

function DeploymentActionProgress({deployment, action}) {
    return (
        <>
            <Progress
                strokeColor={action.overridden ? "orange" : undefined}
                data-testid={`deployment-progress-${deployment.id}`}
                type="circle"
                percent={action.percentage}
                size={32}
            />
        </>
    )
}

function DeploymentActionButton({actionEnabled, text, confirmTitle, confirmDescription, onConfirm, loading}) {
    return (
        <>
            <Popconfirm
                title={confirmTitle}
                description={confirmDescription}
                onConfirm={onConfirm}
            >
                <Button
                    disabled={!actionEnabled}
                    loading={loading}
                >
                    {text}
                </Button>
            </Popconfirm>
        </>
    )
}

function DeploymentRunActionButton({deployment, onChange}) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = async (errors) => {
        messageApi.error(`Could not start the deployment: ${errors}`)
    }

    const onSuccess = async () => {
        onChange()
    }

    const {action, loading} = useDeploymentRunAction({
        deployment,
    })

    return (
        <>
            <DeploymentActionButton
                deployment={deployment}
                actionEnabled={deployment.runAction.ok}
                text="Start running the deployment"
                confirmTitle="Running deployment"
                confirmDescription="Running this deployment may affect some running services. Do you want to continue?"
                onConfirm={action}
                loading={loading}
                onSuccess={onSuccess}
                onError={onError}
            />
            {contextHolder}
        </>
    )
}

function DeploymentFinishActionButton({deployment, onChange}) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = async (errors) => {
        messageApi.error(`Could not finish the deployment: ${errors}`)
    }

    const onSuccess = async () => {
        onChange()
    }

    const {action, loading} = useDeploymentFinishAction({
        deployment,
    })

    return (
        <>
            <DeploymentActionButton
                deployment={deployment}
                status="DONE"
                actionEnabled={deployment.finishAction.ok}
                text="Marks the deployment as complete"
                confirmTitle="Finish deployment"
                confirmDescription="This deployment will be marked as complete. Do you want to continue?"
                onConfirm={action}
                loading={loading}
                onSuccess={onSuccess}
                onError={onError}
            />
            {contextHolder}
        </>
    )
}

function DeploymentCancelActionButton({deployment, onChange}) {

    const [messageApi, contextHolder] = message.useMessage()

    const onError = async (errors) => {
        messageApi.error(`Could not cancel the deployment: ${errors}`)
    }

    const onSuccess = async () => {
        onChange()
    }

    const {action, loading} = useDeploymentCancelAction({
        deployment,
    })

    return (
        <>
            <DeploymentActionButton
                deployment={deployment}
                status="CANCELLED"
                actionEnabled={true}
                text="Cancels the deployment"
                confirmTitle="Cancel deployment"
                confirmDescription="This deployment will be cancelled. Do you want to continue?"
                onConfirm={action}
                loading={loading}
                onSuccess={onSuccess}
                onError={onError}
            />
            {contextHolder}
        </>
    )
}

const deploymentActionButtonStep = ({
                                        actionButton,
                                        actionEnabled,
                                        icon,
                                    }) => {
    return {
        title: actionButton,
        disabled: !actionEnabled,
        icon: icon,
    }
}

function DeploymentActionButtonStep({indicator, actionButton, icon, description}) {
    return (
        <>
            <DeploymentStep
                avatar={icon}
                title={
                    <Space>
                        {indicator}
                        {actionButton}
                    </Space>
                }
                description={description}
            />
        </>
    )
}

export function DeploymentRunButtonStep({deployment, onChange}) {
    return <DeploymentActionButtonStep
        actionButton={
            <DeploymentRunActionButton deployment={deployment} onChange={onChange}/>
        }
        icon={
            <SlotPipelineStatusIcon status="RUNNING"/>
        }
        indicator={
            <DeploymentActionProgress
                deployment={deployment}
                action={deployment.runAction}
            />
        }
    />
}

export function DeploymentFinishButtonStep({deployment, onChange}) {
    return <DeploymentActionButtonStep
        actionButton={
            <DeploymentFinishActionButton deployment={deployment} onChange={onChange}/>
        }
        icon={
            <SlotPipelineStatusIcon status="DONE"/>
        }
    />
}

export const deploymentCancelButtonStep = (deployment, onChange) => {
    return deploymentActionButtonStep({
        deployment,
        actionEnabled: true,
        actionButton: <DeploymentCancelActionButton deployment={deployment} onChange={onChange}/>,
        icon: <SlotPipelineStatusIcon status="CANCELLED"/>,
    })
}
