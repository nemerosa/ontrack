import {Space} from "antd";
import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import SlotPipelineStatusLabel from "@components/extension/environments/SlotPipelineStatusLabel";

export default function SlotPipelineTriggerComponent({pipelineId, status}) {
    return (
        <>
            <Space>
                <SlotPipelineLink pipelineId={pipelineId}/>
                triggered on
                <SlotPipelineStatusLabel status={status}/>
            </Space>
        </>
    )
}