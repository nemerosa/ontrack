import {Handle, Position} from "reactflow";
import {Card, Divider, Space} from "antd";
import EnvironmentTitle from "@components/extension/environments/EnvironmentTitle";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import {
    useProjectEnvironmentsContext
} from "@components/extension/environments/project/ProjectEnvironmentsContextProvider";

export default function SlotGraphNode({data}) {

    const {selectedSlot, setSelectedSlot} = useProjectEnvironmentsContext()

    const onSlotSelected = () => {
        setSelectedSlot(data.slot)
    }

    return (
        <>
            <Handle type="target" position={Position.Left}/>
            <Handle type="source" position={Position.Right}/>

            <Card
                hoverable={true}
                style={{
                    border: selectedSlot?.id === data.slot.id ? 'solid 4px black' : 'solid 2px gray',
                    filter: selectedSlot?.id === data.slot.id ? undefined : 'opacity(33%)',
                }}
            >
                <Space direction="vertical">
                    <div className="ot-action" onClick={onSlotSelected}>
                        <EnvironmentTitle environment={data.slot.environment} tags={false} editable={false}/>
                    </div>
                    {
                        data.slot.lastDeployedPipeline &&
                        <>
                            <Space>
                                <Link href={slotPipelineUri(data.slot.lastDeployedPipeline.id)}>
                                    #{data.slot.lastDeployedPipeline.number}
                                </Link>
                                <Divider type="vertical"/>
                                <BuildLink build={data.slot.lastDeployedPipeline.build}/>
                            </Space>
                            <PromotionRuns promotionRuns={data.slot.lastDeployedPipeline.build.promotionRuns}/>
                        </>
                    }
                </Space>
            </Card>
        </>
    )
}