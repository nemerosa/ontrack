import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {List, Typography} from "antd";
import {slotPipelineStatusLabels} from "@components/extension/environments/SlotPipelineStatusLabel";
import TimestampText from "@components/common/TimestampText";

export const findChange = (deployment, status) => deployment.changes.find(it => it.type === 'STATUS' && it.status === status)

function DeploymentStatusSignature({deployment, status}) {
    const change = findChange(deployment, status)
    return (
        <>
            {
                change &&
                <Typography.Text type="secondary">
                    <TimestampText value={change.timestamp}/> by {change.user}
                </Typography.Text>
            }
        </>
    )
}

export function DeploymentStep({id, avatar, title, description}) {
    return (
        <>
            <List.Item className="ot-list-item" data-testid={id}>
                <List.Item.Meta
                    avatar={avatar}
                    title={title}
                    description={description}
                />
            </List.Item>
        </>
    )
}

function DeploymentStatusStep({deployment, status}) {
    return (
        <>
            <DeploymentStep
                avatar={
                    <SlotPipelineStatusIcon status={status}/>
                }
                title={
                    slotPipelineStatusLabels[status]
                }
                description={
                    <DeploymentStatusSignature deployment={deployment} status={status}/>
                }
            />
        </>
    )
}

export function DeploymentCandidateStatusStep({deployment}) {
    return <DeploymentStatusStep
        deployment={deployment}
        status="CANDIDATE"
    />
}

export function DeploymentRunningStatusStep({deployment}) {
    return <DeploymentStatusStep
        deployment={deployment}
        status="RUNNING"
    />
}

export function DeploymentDoneStatusStep({deployment}) {
    return <DeploymentStatusStep
        deployment={deployment}
        status="DONE"
    />
}

export function DeploymentCancelledStatusStep({deployment}) {
    return <DeploymentStatusStep
        deployment={deployment}
        status="CANCELLED"
    />
}