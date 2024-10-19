import SlotPipelineDeployButton from "@components/extension/environments/SlotPipelineDeployButton";
import SlotPipelineFinishButton from "@components/extension/environments/SlotPipelineFinishButton";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";

export default function SlotPipelineStatusActions({pipeline, onChange}) {
    return (
        <>
            <SlotPipelineStatus pipeline={pipeline}>
                <SlotPipelineDeployButton pipeline={pipeline} onDeploy={onChange}/>
                <SlotPipelineFinishButton pipeline={pipeline} onFinish={onChange}/>
            </SlotPipelineStatus>
        </>
    )
}