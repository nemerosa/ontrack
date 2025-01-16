import {Space} from "antd";
import SlotPipelineStatusLabel from "@components/extension/environments/SlotPipelineStatusLabel";

/**
 * @deprecated
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