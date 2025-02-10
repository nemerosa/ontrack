export default function SlotPipelineTriggerLink({pipelineId, status}) {
    return (
        <>
            {JSON.stringify({pipelineId, status})}
        </>
    )
}