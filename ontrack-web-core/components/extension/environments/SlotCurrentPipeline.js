import {Card, Empty, Space} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import SlotPipelineCard from "@components/extension/environments/SlotPipelineCard";

const fullCardTitle = (prefix, title) => {
    if (prefix) {
        return `${prefix} ${title}`
    } else {
        return title
    }
}

export default function SlotCurrentPipeline({
                                                slot,
                                                actions = true,
                                                slotState,
                                                titlePrefix = '',
                                                showLastDeployed = false
                                            }) {

    const client = useGraphQLClient()

    const [pipelineState, setPipelineState] = useState(0)
    const changePipelineState = () => {
        setPipelineState(i => i + 1)
    }

    const [loading, setLoading] = useState(false)
    const [pipeline, setPipeline] = useState()
    const [lastDeployedPipeline, setLastDeployedPipeline] = useState()
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
                            lastDeployedPipeline {
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
                setLastDeployedPipeline(data.slotById?.lastDeployedPipeline)
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
                    title={fullCardTitle(titlePrefix, "No deployment")}
                >
                    <Empty description=""/>
                </Card>
            }
            {
                pipeline &&
                <Space direction="vertical">
                    <SlotPipelineCard
                        pipeline={pipeline}
                        titlePrefix={titlePrefix}
                        actions={actions}
                        onChange={changePipelineState}
                    />
                    {
                        showLastDeployed && lastDeployedPipeline && lastDeployedPipeline.id !== pipeline.id &&
                        <SlotPipelineCard
                            pipeline={lastDeployedPipeline}
                            titlePrefix="Last deployed: "
                            actions={false}
                        />
                    }
                </Space>
            }
        </>
    )
}