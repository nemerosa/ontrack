import SlotPipelineDeployButton from "@components/extension/environments/SlotPipelineDeployButton";
import SlotPipelineFinishButton from "@components/extension/environments/SlotPipelineFinishButton";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import {Typography} from "antd";
import SlotPipelineDeploymentStatusButton from "@components/extension/environments/SlotPipelineDeploymentStatusButton";

export default function SlotPipelineStatusActions({pipeline, info = true, actions = true, onChange}) {
    return (
        <>
            <SlotPipelineStatus pipeline={pipeline}>
                {
                    info &&
                    <SlotPipelineDeploymentStatusButton
                        pipeline={pipeline}
                    />
                }
                {
                    actions && <>
                        <SlotPipelineDeployButton pipeline={pipeline} onDeploy={onChange}/>
                        <SlotPipelineFinishButton pipeline={pipeline} onFinish={onChange}/>
                        <SlotPipelineCancelButton pipeline={pipeline} onCancel={onChange}/>
                    </>
                }
                {
                    pipeline.status === 'CANCELLED' &&
                    pipeline.lastChange?.message &&
                    <Typography.Text type="secondary">{pipeline.lastChange.message}</Typography.Text>
                }
            </SlotPipelineStatus>
        </>
    )
}