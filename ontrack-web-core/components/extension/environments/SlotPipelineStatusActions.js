import SlotPipelineDeployButton from "@components/extension/environments/SlotPipelineDeployButton";
import SlotPipelineFinishButton from "@components/extension/environments/SlotPipelineFinishButton";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import {Typography} from "antd";
import SlotPipelineDeploymentStatusProgress
    from "@components/extension/environments/SlotPipelineDeploymentStatusProgress";
import SlotPipelineInputButton from "@components/extension/environments/SlotPipelineInputButton";

export default function SlotPipelineStatusActions({pipeline, info = true, linkInfo = true, actions = true, onChange}) {
    return (
        <>
            <SlotPipelineStatus pipeline={pipeline}>
                {
                    info &&
                    <SlotPipelineDeploymentStatusProgress
                        pipeline={pipeline}
                        link={linkInfo}
                    />
                }
                {
                    actions && <>
                        <SlotPipelineInputButton pipeline={pipeline} onChange={onChange}/>
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