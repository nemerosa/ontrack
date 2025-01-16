import SlotPipelineRunButton from "@components/extension/environments/SlotPipelineRunButton";
import {Space, Typography} from "antd";
import SlotPipelineDoneButton from "@components/extension/environments/SlotPipelineDoneButton";
import SlotPipelineCancelButton from "@components/extension/environments/SlotPipelineCancelButton";
import SlotPipelineDeploymentStatusProgress
    from "@components/extension/environments/SlotPipelineDeploymentStatusProgress";
import SlotPipelineStatusLabel from "@components/extension/environments/SlotPipelineStatusLabel";
import SlotPipelineInputButton from "@components/extension/environments/SlotPipelineInputButton";

export default function SlotPipelineStatusActions({
                                                      pipeline,
                                                      info = true,
                                                      linkInfo = true,
                                                      actions = true,
                                                      size,
                                                      showMessageOnCancelled = true,
                                                      showStatus = true,
                                                      reloadState,
                                                      onChange
                                                  }) {

    return (
        <>
            <Space data-testid={`pipeline-actions-${pipeline.id}`}>
                {
                    showStatus &&
                    <SlotPipelineStatusLabel
                        status={pipeline.status}
                        showText={true}
                    />
                }
                {
                    info && pipeline.status === "CANDIDATE" &&
                    <SlotPipelineDeploymentStatusProgress
                        pipeline={pipeline}
                        link={linkInfo}
                        reloadState={reloadState}
                        size={size}
                    />
                }
                {
                    actions && <>
                        <SlotPipelineInputButton pipeline={pipeline} reloadState={reloadState} onChange={onChange} size={size}/>
                        <SlotPipelineRunButton pipeline={pipeline} reloadState={reloadState} onDeploy={onChange} size={size}/>
                        <SlotPipelineDoneButton pipeline={pipeline} reloadState={reloadState} onFinish={onChange} size={size}/>
                        <SlotPipelineCancelButton deployment={pipeline} reloadState={reloadState} onCancel={onChange} size={size}/>
                    </>
                }
                {
                    pipeline.status === 'CANCELLED' &&
                    showMessageOnCancelled &&
                    pipeline.lastChange?.message &&
                    <Typography.Text type="secondary">{pipeline.lastChange.message}</Typography.Text>
                }
            </Space>
        </>
    )
}