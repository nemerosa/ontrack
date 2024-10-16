import {Card, Typography} from "antd";
import SlotTitle from "@components/extension/environments/SlotTitle";

export default function SlotCard({slot}) {
    return (
        <>
            <Card
                style={{
                    height: '100%',
                }}
                title={<SlotTitle slot={slot}/>}
            >
                <Typography.Text type="secondary">{slot.description}</Typography.Text>
            </Card>
        </>
    )
}