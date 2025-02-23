import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {List, Space, Typography} from "antd";
import {slotPipelineStatusLabels} from "@components/extension/environments/SlotPipelineStatusLabel";
import TimestampText from "@components/common/TimestampText";
import {FaExclamationTriangle} from "react-icons/fa";

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

function DeploymentStatusMessage({deployment, status}) {
    const change = findChange(deployment, status)
    return (
        <>
            {
                change && change.overrideMessage &&
                <Space>
                    <FaExclamationTriangle color="red"/>
                    <Typography.Text strong italic data-testid={`${deployment.id}-status-message-${status}`}>
                        {change.overrideMessage}
                    </Typography.Text>
                </Space>
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
                id={`${deployment.id}-status-${status}`}
                avatar={
                    <SlotPipelineStatusIcon status={status}/>
                }
                title={
                    slotPipelineStatusLabels[status]
                }
                description={
                    <Space>
                        <DeploymentStatusSignature deployment={deployment} status={status}/>
                        <DeploymentStatusMessage deployment={deployment} status={status}/>
                    </Space>
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