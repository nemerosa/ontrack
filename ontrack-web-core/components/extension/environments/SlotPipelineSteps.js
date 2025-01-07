import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {Steps} from "antd";
import {useEffect, useState} from "react";
import {
    deploymentCandidateStatusStep,
    deploymentDoneStatusStep,
    deploymentRunningStatusStep
} from "@components/extension/environments/deployment/steps/deploymentStatusSteps";
import {
    candidateAdmissionRuleStep
} from "@components/extension/environments/deployment/steps/candidateAdmissionRuleStep";
import {deploymentWorkflowStep} from "@components/extension/environments/deployment/steps/deploymentWorkflowStep";
import {
    deploymentCancelButtonStep,
    deploymentFinishButtonStep,
    deploymentRunButtonStep
} from "@components/extension/environments/deployment/steps/deploymentActionSteps";

const candidateStatus = (deployment) => {
    return deploymentCandidateStatusStep({deployment})
}

const candidateAdmissionRules = (pipeline) => {
    return pipeline.admissionRules.map(rule =>
        candidateAdmissionRuleStep(pipeline, rule)
    )
}

const candidateWorkflows = (pipeline) => {
    return pipeline.slot.candidateWorkflows.map(slotWorkflow =>
        deploymentWorkflowStep(pipeline, slotWorkflow)
    )
}

const runButton = (deployment, onChange) => {
    return deploymentRunButtonStep(deployment, onChange)
}

const runningStatus = (deployment) => {
    return deploymentRunningStatusStep({deployment})
}

const runningWorkflows = (pipeline) => {
    return pipeline.slot.runningWorkflows.map(slotWorkflow =>
        deploymentWorkflowStep(pipeline, slotWorkflow)
    )
}

const finishButton = (deployment, onChange) => {
    return deploymentFinishButtonStep(deployment, onChange)
}

const finishStatus = (deployment) => {
    return deploymentDoneStatusStep({deployment})
}

const doneWorkflows = (pipeline) => {
    return pipeline.slot.doneWorkflows.map(slotWorkflow =>
        deploymentWorkflowStep(pipeline, slotWorkflow)
    )
}

const cancelButton = (deployment, onChange) => {
    return deploymentCancelButtonStep(deployment, onChange)
}

const generateItems = (pipeline, onChange) => {

    // 1 - Candidate

    if (pipeline.status === 'CANDIDATE') {
        const items = [
            candidateStatus(pipeline),
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
        ]
        if (pipeline.runAction) {
            items.push(runButton(pipeline, onChange))
        }
        items.push(cancelButton(pipeline, onChange))
        return {
            items: items,
            current: 0,
        }
    }

    // 2 - Running

    else if (pipeline.status === 'RUNNING') {
        const items = [
            candidateStatus(pipeline),
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
            runningStatus(pipeline),
            ...runningWorkflows(pipeline),
        ]
        if (pipeline.finishAction) {
            items.push(finishButton(pipeline, onChange))
        }
        items.push(cancelButton(pipeline, onChange))
        return {
            items: items,
            current: 0,
        }
    }

    // 3 - Cancelled

    else if (pipeline.status === 'CANCELLED') {
        // TODO Previous state taken from the changes
        return {
            items: [],
            current: 0,
        }
    }

    // 4 - Done

    else if (pipeline.status === 'DONE') {
        const items = [
            candidateStatus(pipeline),
            ...candidateAdmissionRules(pipeline),
            ...candidateWorkflows(pipeline),
            runningStatus(pipeline),
            ...runningWorkflows(pipeline),
            finishStatus(pipeline),
            ...doneWorkflows(pipeline),
        ];
        return {
            items: items,
            current: items.length - 1,
        }
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
    const [current, setCurrent] = useState(0)
    useEffect(() => {
        if (data) {
            const pipeline = data.slotPipelineById
            const {items, current} = generateItems(pipeline, onChange)
            setItems(items)
            setCurrent(current)
        }
    }, [data])

    return (
        <>
            <Steps
                loading={loading}
                items={items}
                current={current}
                direction="vertical"
            />
            {/*<pre>*/}
            {/*    {JSON.stringify(data, null, 2)}*/}
            {/*</pre>*/}
        </>
    )
}