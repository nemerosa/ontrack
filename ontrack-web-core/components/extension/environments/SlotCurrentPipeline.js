import {Card, Descriptions, Empty, Space} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import TimestampText from "@components/common/TimestampText";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";

export default function SlotCurrentPipeline({slot, slotState}) {

    const client = useGraphQLClient()

    const [pipelineState, setPipelineState] = useState(0)
    const changePipelineState = () => {
        setPipelineState(i => i + 1)
    }

    const [loading, setLoading] = useState(false)
    const [pipeline, setPipeline] = useState()
    const [items, setItems] = useState([])
    useEffect(() => {
        if (client && slot) {
            setLoading(true)
            client.request(
                gql`
                    query SlotPipeline($id: String!) {
                        slotById(id: $id) {
                            currentPipeline {
                                ...SlotPipelineData
                            }
                        }
                    }
                    ${gqlSlotPipelineData}
                `,
                {
                    id: slot.id,
                }
            ).then(data => {
                const pipeline = data.slotById?.currentPipeline;
                setPipeline(pipeline)
                if (pipeline) {
                    const items = [
                        {
                            key: 'status',
                            label: 'Status',
                            children: <SlotPipelineStatusActions pipeline={pipeline} onChange={changePipelineState}/>,
                            span: 12,
                        },
                        {
                            key: 'start',
                            label: 'Started at',
                            children: <TimestampText value={pipeline.start}/>,
                            span: 12,
                        },
                    ]
                    if (pipeline.end) {
                        items.push({
                            key: 'end',
                            label: 'Ended at',
                            children: <TimestampText value={pipeline.end}/>,
                            span: 12,
                        })
                    }
                    items.push({
                        key: 'build',
                        label: 'Build',
                        children: <Space>
                            <BuildLink build={pipeline.build}/>
                            <PromotionRuns promotionRuns={pipeline.build.promotionRuns}/>
                        </Space>,
                        span: 12,
                    })
                    setItems(items)
                } else {
                    setItems([])
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, slot, slotState, pipelineState])

    return (
        <>
            {
                !pipeline &&
                <Card
                    loading={loading}
                    title="No pipeline"
                >
                    <Empty description=""/>
                </Card>
            }
            {
                pipeline &&
                <Card
                    loading={loading}
                    title={`Pipeline #${pipeline.number}`}
                >
                    <Descriptions
                        items={items}
                    />
                </Card>
            }
        </>
    )
}