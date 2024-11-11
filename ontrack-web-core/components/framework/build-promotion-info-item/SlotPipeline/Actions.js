import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";

export default function SlotPipelineBuildPromotionInfoItemActions({item, onChange}) {
    return (
        <>
            <SlotPipelineStatusActions
                pipeline={item}
                showMessageOnCancelled={false}
                onChange={onChange}
            />
        </>
    )
}