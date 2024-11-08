import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";

export default function SlotPipelineCreationWorkflowNodeExecutorOutput({data}) {

    const {targetPipelineId} = data

    return (
        <>
            <SlotPipelineLink pipelineId={targetPipelineId} status={true}/>
        </>
    )
}