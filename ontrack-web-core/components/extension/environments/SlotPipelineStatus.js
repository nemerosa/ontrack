import {Space} from "antd";
import SlotPipelineStatusLabel from "@components/extension/environments/SlotPipelineStatusLabel";

/**
 * @deprecated Use SlotPipelineStatusLabel instead
 */
export default function SlotPipelineStatus({pipeline, showText = true, children}) {
    return (
        <>
            <Space data-testid={`pipeline-actions-${pipeline.id}`}>
                <SlotPipelineStatusLabel
                    status={pipeline.status}
                    showText={showText}
                />
                {children}
            </Space>
        </>
    )
}