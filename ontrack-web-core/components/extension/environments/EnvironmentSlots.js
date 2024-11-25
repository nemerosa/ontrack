import {Space} from "antd";
import EnvironmentSlotInline from "@components/extension/environments/EnvironmentSlotInline";

export default function EnvironmentSlots({slots = []}) {
    return (
        <>
            <Space>
                {
                    slots.map(slot => <EnvironmentSlotInline key={slot.id} slot={slot}/>)
                }
            </Space>
        </>
    )
}