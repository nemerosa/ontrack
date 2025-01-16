import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";
import {useEffect, useState} from "react";
import {Descriptions, Space} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import TimestampText from "@components/common/TimestampText";

export default function SlotPipelineSummary({pipelineId, reloadState}) {

    const {loading, data} = useQuery(
        gql`
            query PipelineDetails($id: String!) {
                slotPipelineById(id: $id) {
                    id
                    build {
                        ...SlotPipelineBuildData
                    }
                    status
                    start
                    end
                }
            }
            ${gqlSlotPipelineBuildData}
        `,
        {
            variables: {
                id: pipelineId
            },
            deps: [reloadState],
        }
    )

    const [items, setItems] = useState([])
    useEffect(() => {

        if (data) {
            const pipeline = data.slotPipelineById

            const span = pipeline.end ? 2 : 3

            const items = []

            items.push({
                key: 'build',
                label: "Build",
                span: span,
                children: <Space>
                    <BuildLink build={pipeline.build}/>
                    <PromotionRuns promotionRuns={pipeline.build.promotionRuns}/>
                </Space>,
            })

            items.push({
                key: 'start',
                label: 'Started at',
                span: span,
                children: <TimestampText value={pipeline.start}/>,
            })

            if (pipeline.end) {
                items.push({
                    key: 'end',
                    label: 'Ended at',
                    span: span,
                    children: <TimestampText value={pipeline.end}/>,
                })
            }

            setItems(items)
        }
    }, [data])

    return (
        <>
            <Descriptions
                loading={loading}
                items={items}
                column={6}
                layout="horizontal"
            />
        </>
    )
}