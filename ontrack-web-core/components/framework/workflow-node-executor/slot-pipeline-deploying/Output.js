import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";

export default function SlotPipelineDeployingWorkflowNodeExecutorOutput({data}) {

    const {pipelineId} = data

    return (
        <>
            <SlotPipelineLink pipelineId={pipelineId} status={true}/>
        </>
    )
}