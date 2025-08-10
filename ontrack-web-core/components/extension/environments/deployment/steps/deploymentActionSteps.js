import {Progress, Space} from "antd";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import SlotPipelineDoneButton from "@components/extension/environments/SlotPipelineDoneButton";
import SlotPipelineRunButton from "@components/extension/environments/SlotPipelineRunButton";

function DeploymentActionProgress({deployment, action}) {
    return (
        <>
            <Progress
                strokeColor={action.overridden ? "orange" : undefined}
                data-testid={`deployment-progress-${deployment.id}`}
                className={
                    action.overridden ? `ot-extension-environment-overridden` : undefined
                }
                type="circle"
                percent={action.percentage}
                size={32}
            />
        </>
    )
}

function DeploymentActionButtonStep({id, indicator, actionButton, icon, description}) {
    return (
        <>
            <DeploymentStep
                id={id}
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

export function DeploymentRunButtonStep({deployment, reloadState, onChange}) {
    return <DeploymentActionButtonStep
        id={`deployment-run-${deployment.id}`}
        actionButton={
            <SlotPipelineRunButton
                pipeline={deployment}
                reloadState={reloadState}
                onDeploy={onChange}
                showIcon={false}
                showText={true}
                showDisabledButtonIfNotOk={true}
            />
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

export function DeploymentFinishButtonStep({deployment, reloadState, onChange}) {
    return <DeploymentActionButtonStep
        actionButton={
            <SlotPipelineDoneButton
                pipeline={deployment}
                reloadState={reloadState}
                onFinish={onChange}
                showDisabledButtonIfNotOk={true}
                showIcon={false}
                showText={true}
                color="primary"
                variant="solid"
            />
        }
        icon={
            <SlotPipelineStatusIcon status="DONE"/>
        }
    />
}

export function DeploymentCancelButtonStep({deployment, reloadState, onChange}) {
    return <DeploymentActionButtonStep
        actionButton={
            <SlotPipelineCancelButton
                deployment={deployment}
                reloadState={reloadState}
                onCancel={onChange}
                showText={true}
                showIcon={false}
                color="danger"
                variant="solid"
            />
        }
        icon={
            <SlotPipelineStatusIcon status="CANCELLED"/>
        }
    />
}
