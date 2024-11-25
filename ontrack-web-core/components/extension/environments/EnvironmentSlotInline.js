import {Space} from "antd";
import SlotTitle from "@components/extension/environments/SlotTitle";
import SlotLink from "@components/extension/environments/SlotLink";

export default function EnvironmentSlotInline({slot}) {
    return (
        <>
            <Space>
                <SlotTitle slot={slot}/>
                <SlotLink slot={slot}/>
            </Space>
        </>
    )
}