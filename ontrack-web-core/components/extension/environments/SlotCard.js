import {Card, Divider, Space, Typography} from "antd";
import SlotTitle from "@components/extension/environments/SlotTitle";
import SlotEligibleBuild from "@components/extension/environments/SlotEligibleBuild";

export default function SlotCard({slot}) {
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
                    />
                </Space>
            </Card>
        </>
    )
}