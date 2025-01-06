import SlotPipelineRunButton from "@components/extension/environments/SlotPipelineRunButton";
import SlotPipelineDoneButton from "@components/extension/environments/SlotPipelineDoneButton";
import SlotPipelineStatus from "@components/extension/environments/SlotPipelineStatus";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import {Typography} from "antd";
import SlotPipelineDeploymentStatusProgress
    from "@components/extension/environments/SlotPipelineDeploymentStatusProgress";
import SlotPipelineInputButton from "@components/extension/environments/SlotPipelineInputButton";
import {useReloadState} from "@components/common/StateUtils";

export default function SlotPipelineStatusActions({pipeline, info = true, linkInfo = true, actions = true, size, showMessageOnCancelled = true, onChange}) {

    const [reloadState, reload] = useReloadState({callback: onChange})

    return (
        <>
            <SlotPipelineStatus pipeline={pipeline}>
                {
                    info &&
                    <SlotPipelineDeploymentStatusProgress
                        pipeline={pipeline}
                        link={linkInfo}
                        reloadState={reloadState}
                        size={size}
                    />
                }
                {
                    actions && <>
                        <SlotPipelineInputButton pipeline={pipeline} onChange={reload} size={size}/>
                        <SlotPipelineRunButton pipeline={pipeline} onDeploy={reload} size={size}/>
                        <SlotPipelineDoneButton pipeline={pipeline} onFinish={reload} size={size}/>
                        <SlotPipelineCancelButton pipeline={pipeline} onCancel={reload} size={size}/>
                    </>
                }
                {
                    pipeline.status === 'CANCELLED' &&
                    showMessageOnCancelled &&
                    pipeline.lastChange?.message &&
                    <Typography.Text type="secondary">{pipeline.lastChange.message}</Typography.Text>
                }
            </SlotPipelineStatus>
        </>
    )
}