import {FaProjectDiagram, FaRegHourglass} from "react-icons/fa";
import {Space} from "antd";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import WorkflowInstanceLink from "@components/extension/workflows/WorkflowInstanceLink";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";

function DeploymentWorkflowTimings({slotWorkflow}) {
    const slotWorkflowInstance = slotWorkflow.slotWorkflowInstanceForPipeline
    if (slotWorkflowInstance) {
        return <>
            Started at <TimestampText
            value={slotWorkflowInstance.workflowInstance.startTime}/>
            {
                slotWorkflowInstance.workflowInstance.endTime && <>
                    &nbsp;(<DurationMs ms={slotWorkflowInstance.workflowInstance.durationMs}/>)
                </>
            }
        </>
    } else {
        return ''
    }
}

function DeploymentWorkflowStepStatusNotStarted({slotWorkflow}) {
    return <Space>
        <FaRegHourglass/>
        Not started
    </Space>
}

function DeploymentWorkflowStepStatus({slotWorkflow}) {
    const slotWorkflowInstance = slotWorkflow.slotWorkflowInstanceForPipeline
    if (!slotWorkflowInstance) {
        return <DeploymentWorkflowStepStatusNotStarted slotWorkflow={slotWorkflow}/>
    } else {
        return <WorkflowInstanceStatus status={slotWorkflowInstance.workflowInstance.status}/>
    }
}

export const deploymentWorkflowStep = (deployment, slotWorkflow) => {
    return {
        title: <Space>
            {/* Workflow status */}
            <DeploymentWorkflowStepStatus slotWorkflow={slotWorkflow}/>
            {/* Workflow name */}
            {
                slotWorkflow.slotWorkflowInstanceForPipeline &&
                <WorkflowInstanceLink
                    workflowInstanceId={slotWorkflow.slotWorkflowInstanceForPipeline.id}
                    name={slotWorkflow.workflow.name}
                />
            }
            {
                !slotWorkflow.slotWorkflowInstanceForPipeline &&
                <strong>{slotWorkflow.workflow.name}</strong>
            }
        </Space>,
        description: <Space>
            <DeploymentWorkflowTimings slotWorkflow={slotWorkflow}/>
        </Space>,
        icon: <FaProjectDiagram title="Workflow"/>,
    }
}
