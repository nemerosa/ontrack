import {FaProjectDiagram, FaRegHourglass} from "react-icons/fa";
import {Space} from "antd";
import WorkflowInstanceStatus from "@components/extension/workflows/WorkflowInstanceStatus";
import WorkflowInstanceLink from "@components/extension/workflows/WorkflowInstanceLink";
import TimestampText from "@components/common/TimestampText";
import DurationMs from "@components/common/DurationMs";
import {DeploymentStep} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import SlotPipelineOverrideWorkflowButton from "@components/extension/environments/SlotPipelineOverrideWorkflowButton";
import SlotPipelineOverrideIndicator from "@components/extension/environments/SlotPipelineOverrideIndicator";

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

function DeploymentWorkflowStepStatusNotStarted({id, slotWorkflow}) {
    return <Space data-testid={id}>
        <FaRegHourglass/>
        Not started
    </Space>
}

function DeploymentWorkflowStepStatus({slotWorkflow}) {
    const slotWorkflowInstance = slotWorkflow.slotWorkflowInstanceForPipeline
    if (!slotWorkflowInstance) {
        return <DeploymentWorkflowStepStatusNotStarted
            id={`slot-workflow-instance-status-${slotWorkflow.id}`}
            slotWorkflow={slotWorkflow}
        />
    } else {
        return <WorkflowInstanceStatus
            id={`slot-workflow-instance-status-${slotWorkflow.id}`}
            status={slotWorkflowInstance.workflowInstance.status}
        />
    }
}

export function DeploymentWorkflowStep({deployment, slotWorkflow, disabled = false, onChange}) {
    return (
        <>
            <DeploymentStep
                id={`slot-workflow-${slotWorkflow.id}`}
                avatar={<FaProjectDiagram title="Workflow"/>}
                title={
                    <Space>
                        <DeploymentWorkflowStepStatus slotWorkflow={slotWorkflow}/>
                        {/* Overriding */}
                        {
                            !disabled && slotWorkflow.slotWorkflowInstanceForPipeline &&
                            <SlotPipelineOverrideWorkflowButton
                                deployment={deployment}
                                slotWorkflow={slotWorkflow}
                                slotWorkflowInstance={slotWorkflow.slotWorkflowInstanceForPipeline}
                                onChange={onChange}
                            />
                        }
                        {/* Overridden */}
                        {
                            slotWorkflow.slotWorkflowInstanceForPipeline &&
                            <SlotPipelineOverrideIndicator
                                container={slotWorkflow.slotWorkflowInstanceForPipeline}
                                id={slotWorkflow.id}
                                message="The result of this workflow was overridden"
                            />
                        }
                        {
                            slotWorkflow.slotWorkflowInstanceForPipeline &&
                            <WorkflowInstanceLink
                                id={`slot-workflow-instance-link-${slotWorkflow.id}`}
                                workflowInstanceId={slotWorkflow.slotWorkflowInstanceForPipeline.workflowInstance.id}
                                name={slotWorkflow.workflow.name}
                            />
                        }
                        {
                            !slotWorkflow.slotWorkflowInstanceForPipeline &&
                            <strong data-testid={`slot-workflow-instance-link-${slotWorkflow.id}`}>{slotWorkflow.workflow.name}</strong>
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
