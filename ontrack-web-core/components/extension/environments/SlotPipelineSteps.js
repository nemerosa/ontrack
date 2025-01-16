import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {List} from "antd";
import {useEffect, useState} from "react";
import {
    DeploymentCandidateStatusStep,
    DeploymentDoneStatusStep,
    DeploymentRunningStatusStep
} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import {
    deploymentCancelButtonStep,
    DeploymentFinishButtonStep,
    DeploymentRunButtonStep
} from "@components/extension/environments/deployment/steps/deploymentActionSteps";
import {
    CandidateAdmissionRuleStep
} from "@components/extension/environments/deployment/steps/CandidateAdmissionRuleStep";
import {DeploymentWorkflowStep} from "@components/extension/environments/deployment/steps/DeploymentWorkflowStep";

function CandidateStatus({deployment}) {
    return <DeploymentCandidateStatusStep deployment={deployment}/>
}

const candidateAdmissionRules = (pipeline) => {
    return pipeline.admissionRules.map(rule =>
        <CandidateAdmissionRuleStep key={rule.admissionRuleConfig.id} rule={rule}/>
    )
}

const candidateWorkflows = (pipeline) => {
    return pipeline.slot.candidateWorkflows.map(slotWorkflow =>
        <DeploymentWorkflowStep key={slotWorkflow.id} slotWorkflow={slotWorkflow}/>
    )
}

const runButton = (deployment, onChange) => {
    return <DeploymentRunButtonStep deployment={deployment} onChange={onChange}/>
}

function RunningStatus({deployment}) {
    return <DeploymentRunningStatusStep deployment={deployment}/>
}

const runningWorkflows = (pipeline) => {
    return pipeline.slot.runningWorkflows.map(slotWorkflow =>
        <DeploymentWorkflowStep key={slotWorkflow.id} slotWorkflow={slotWorkflow}/>
    )
}

const finishButton = (deployment, onChange) => {
    return <DeploymentFinishButtonStep deployment={deployment} onChange={onChange}/>
}

function FinishStatus({deployment}) {
    return <DeploymentDoneStatusStep deployment={deployment}/>
}

const doneWorkflows = (pipeline) => {
    return pipeline.slot.doneWorkflows.map(slotWorkflow =>
        <DeploymentWorkflowStep key={slotWorkflow.id} slotWorkflow={slotWorkflow}/>
    )
}

const cancelButton = (deployment, onChange) => {
    return deploymentCancelButtonStep(deployment, onChange)
}

const generateItems = (pipeline, onChange) => {

    // 1 - Candidate

    if (pipeline.status === 'CANDIDATE') {
        const items = [
            <CandidateStatus key="status" deployment={pipeline}/>,
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
        ]
        if (pipeline.runAction) {
            items.push(runButton(pipeline, onChange))
        }
        // TODO items.push(cancelButton(pipeline, onChange))
        return items
    }

    // 2 - Running

    else if (pipeline.status === 'RUNNING') {
        const items = [
            <CandidateStatus key="status-candidate" deployment={pipeline}/>,
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
            <RunningStatus key="status-running" deployment={pipeline}/>,
            ...runningWorkflows(pipeline),
        ]
        if (pipeline.finishAction) {
            items.push(finishButton(pipeline, onChange))
        }
        // TODO items.push(cancelButton(pipeline, onChange))
        return items
    }

    // 3 - Cancelled

    else if (pipeline.status === 'CANCELLED') {
        // TODO Previous state taken from the changes
        return []
    }

    // 4 - Done

    else if (pipeline.status === 'DONE') {
        return [
            <CandidateStatus key="status-candidate" deployment={pipeline}/>,
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
            <RunningStatus key="status-running" deployment={pipeline}/>,
            ...runningWorkflows(pipeline),
            <FinishStatus key="status-done" deployment={pipeline}/>,
            ...doneWorkflows(pipeline),
        ]
    }

    // Unknown

    else {
        return []
    }

}

export default function SlotPipelineSteps({pipelineId, reloadState, onChange}) {

    const {loading, data} = useQuery(
        gql`

            fragment SlotWorkflowContent on SlotWorkflow {
                id
                workflow {
                    name
                }
            }

            fragment SlotWorkflowInstanceContent on SlotWorkflowInstance {
                id
                workflowInstance {
                    status
                    startTime
                    endTime
                    durationMs
                }
            }

            query SlotPipelineSteps($pipelineId: String!) {
                slotPipelineById(id: $pipelineId) {
                    id
                    status
                    changes {
                        user
                        timestamp
                        type
                        message
                        status
                        overrideMessage
                    }
                    admissionRules {
                        check {
                            ok
                            reason
                        }
                        admissionRuleConfig {
                            id
                            name
                            description
                            ruleId
                            ruleConfig
                        }
                        data {
                            user
                            timestamp
                            data
                        }
                        canBeOverridden
                        overridden
                        override {
                            user
                            timestamp
                            message
                        }
                    }
                    slot {
                        candidateWorkflows: workflows(trigger: CANDIDATE) {
                            ...SlotWorkflowContent
                            slotWorkflowInstanceForPipeline(pipelineId: $pipelineId) {
                                ...SlotWorkflowInstanceContent
                            }
                        }
                        runningWorkflows: workflows(trigger: RUNNING) {
                            ...SlotWorkflowContent
                            slotWorkflowInstanceForPipeline(pipelineId: $pipelineId) {
                                ...SlotWorkflowInstanceContent
                            }
                        }
                        doneWorkflows: workflows(trigger: DONE) {
                            ...SlotWorkflowContent
                            slotWorkflowInstanceForPipeline(pipelineId: $pipelineId) {
                                ...SlotWorkflowInstanceContent
                            }
                        }
                    }
                    runAction {
                        ok
                        overridden
                        successCount
                        totalCount
                        percentage
                    }
                    finishAction {
                        ok
                        overridden
                        successCount
                        totalCount
                        percentage
                    }
                }
            }
        `,
        {
            variables: {pipelineId},
            deps: reloadState,
        }
    )

    const [items, setItems] = useState([])
    useEffect(() => {
        if (data) {
            const pipeline = data.slotPipelineById
            const items = generateItems(pipeline, onChange)
            setItems(items)
        }
    }, [data])

    return (
        <>
            <List
                itemLayout="horizontal"
                loading={loading}
            >
                {items}
            </List>
        </>
    )
}