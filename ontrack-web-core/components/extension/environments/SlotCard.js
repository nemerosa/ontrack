import {Card, Divider, Space, Typography} from "antd";
import SlotTitle from "@components/extension/environments/SlotTitle";
import SlotEligibleBuild from "@components/extension/environments/SlotEligibleBuild";
import SlotCurrentPipeline from "@components/extension/environments/SlotCurrentPipeline";
import useSlotState from "@components/extension/environments/SlotState";

export default function SlotCard({slot}) {

    const [slotState, onSlotStateChanged] = useSlotState()

    return (
        <>
            <Card
                style={{
                    height: '100%',
                }}
                title={<SlotTitle slot={slot}/>}
            >
                <Space direction="vertical" className="ot-line">
                    {
                        slot.description && <>
                            <Typography.Text type="secondary">{slot.description}</Typography.Text>
                            <Divider type="horizontal"/>
                        </>
                    }
                    <SlotEligibleBuild
                        slot={slot}
                        onStart={onSlotStateChanged}
                    />
                    <SlotCurrentPipeline
                        slot={slot}
                        slotState={slotState}
                    />
                </Space>
            </Card>
        </>
    )
}