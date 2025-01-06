import {Space} from "antd";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";

export default function SlotPipelineStatus({pipeline, showText = true, children}) {
    return (
        <>
            <Space data-testid={`pipeline-actions-${pipeline.id}`}>
                <SlotPipelineStatusIcon
                    status={pipeline.status}
                    showText={showText}
                />
                {children}
            </Space>
        </>
    )
}