import {Space} from "antd";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";

export default function SlotBuildPromotionInfoItemActions({item, build, onChange}) {
    return (
        <>
            <Space>
                <SlotPipelineCreateButton
                    slot={item}
                    build={build}
                    onStart={onChange}
                    size="small"
                />
            </Space>
        </>
    )
}