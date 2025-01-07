import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import {Space, Typography} from "antd";
import {slotPipelineStatusLabels} from "@components/extension/environments/SlotPipelineStatusLabel";
import TimestampText from "@components/common/TimestampText";

const findChange = (deployment, status) => deployment.changes.find(it => it.type === 'STATUS' && it.status === status)

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

function deploymentStatusStep({deployment, status}) {
    return {
        title: <Space>
            {slotPipelineStatusLabels[status]}
        </Space>,
        description: <Space direction="vertical">
            <DeploymentStatusSignature deployment={deployment} status={status}/>
        </Space>,
        status: deployment.status === status ? 'process' : 'finish',
        icon: <SlotPipelineStatusIcon status={status}/>,
    }
}

export function deploymentCandidateStatusStep({deployment}) {
    return deploymentStatusStep({deployment, status: 'CANDIDATE'})
}

export function deploymentRunningStatusStep({deployment}) {
    return deploymentStatusStep({deployment, status: 'RUNNING'})
}

export function deploymentDoneStatusStep({deployment}) {
    return deploymentStatusStep({deployment, status: 'DONE'})
}