import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";

export default function SlotPipelineDeployedWorkflowNodeExecutorOutput({data}) {

    const {pipelineId} = data

    return (
        <>
            <SlotPipelineLink pipelineId={pipelineId} status={true}/>
        </>
    )
}