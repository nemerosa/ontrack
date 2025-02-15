import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";

export default function DeploymentTemplatingContextComponent({slotPipelineId}) {
    return (
        <>
            <SlotPipelineLink pipelineId={slotPipelineId}/>
        </>
    )
}