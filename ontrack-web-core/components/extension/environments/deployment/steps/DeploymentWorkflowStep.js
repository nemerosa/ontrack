import {FaProjectDiagram, FaRegHourglass} from "react-icons/fa";
import {Space} from "antd";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import WorkflowInstanceLink from "@components/extension/workflows/WorkflowInstanceLink";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";

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

export function DeploymentWorkflowStep({slotWorkflow}) {
    return (
        <>
            <DeploymentStep
                avatar={<FaProjectDiagram title="Workflow"/>}
                title={
                    <Space>
                        <DeploymentWorkflowStepStatus slotWorkflow={slotWorkflow}/>
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
                    </Space>
                }
                description={
                    <DeploymentWorkflowTimings slotWorkflow={slotWorkflow}/>
                }
            />
        </>
    )
}
