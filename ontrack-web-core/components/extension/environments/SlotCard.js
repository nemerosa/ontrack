import {Card, Divider, Space, Typography} from "antd";
import SlotTitle from "@components/extension/environments/SlotTitle";
import SlotEligibleBuild from "@components/extension/environments/SlotEligibleBuild";
import SlotCurrentPipeline from "@components/extension/environments/SlotCurrentPipeline";
import useSlotState from "@components/extension/environments/SlotState";
import SlotLink from "@components/extension/environments/SlotLink";

export default function SlotCard({
                                     slot,
                                     title,
                                     showLastDeployed = false,
                                     showLastDeployedInTitle = false,
                                     showEligible = true,
                                 }) {

    const [slotState, onSlotStateChanged] = useSlotState()

    return (
        <>
            <Card
                style={{
                    height: '100%',
                }}
                size="small"
                title={title ?? <SlotTitle slot={slot} showLastDeployed={showLastDeployedInTitle}/>}
                extra={
                    <SlotLink slot={slot}/>
                }
            >
                <Space direction="vertical" className="ot-line">
                    {
                        slot.description && <>
                            <Typography.Text type="secondary">{slot.description}</Typography.Text>
                            <Divider type="horizontal"/>
                        </>
                    }
                    <SlotCurrentPipeline
                        slot={slot}
                        slotState={slotState}
                        showLastDeployed={showLastDeployed}
                    />
                    {
                        showEligible &&
                        <SlotEligibleBuild
                            slot={slot}
                            onStart={onSlotStateChanged}
                        />
                    }
                </Space>
            </Card>
        </>
    )
}