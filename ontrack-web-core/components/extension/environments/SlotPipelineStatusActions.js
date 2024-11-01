import SlotPipelineDeployButton from "@components/extension/environments/SlotPipelineDeployButton";
import SlotPipelineFinishButton from "@components/extension/environments/SlotPipelineFinishButton";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import {Typography} from "antd";
import SlotPipelineDeploymentStatusProgress
    from "@components/extension/environments/SlotPipelineDeploymentStatusProgress";
import SlotPipelineInputButton from "@components/extension/environments/SlotPipelineInputButton";
import {useReloadState} from "@components/common/StateUtils";

export default function SlotPipelineStatusActions({pipeline, info = true, linkInfo = true, actions = true, onChange}) {

    const [reloadState, reload] = useReloadState({callback: onChange})

    return (
        <>
            <SlotPipelineStatus pipeline={pipeline}>
                {
                    info && pipeline.status === 'ONGOING' &&
                    <SlotPipelineDeploymentStatusProgress
                        pipeline={pipeline}
                        link={linkInfo}
                        reloadState={reloadState}
                    />
                }
                {
                    actions && <>
                        <SlotPipelineInputButton pipeline={pipeline} onChange={reload}/>
                        <SlotPipelineDeployButton pipeline={pipeline} onDeploy={reload}/>
                        <SlotPipelineFinishButton pipeline={pipeline} onFinish={reload}/>
                        <SlotPipelineCancelButton pipeline={pipeline} onCancel={reload}/>
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