import {useContext, useEffect, useState} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import PromotionLevelLeadTimeChart from "@components/promotionLevels/PromotionLevelLeadTimeChart";
import {PromotionLevelImage} from "@components/promotionLevels/PromotionLevelImage";
import {Space, Typography} from "antd";
import PromotionLevelTTRChart from "@components/promotionLevels/PromotionLevelTTRChart";
import ChartOptions from "@components/charts/ChartOptions";

export default function PromotionTTRChartWidget({project, branch, promotionLevel, interval, period}) {

    const client = useGraphQLClient()

    const {setTitle, setExtra} = useContext(DashboardWidgetCellContext)

    const [promotionLevelObject, setPromotionLevelObject] = useState(undefined)

    useEffect(() => {
        if (client && project && branch && promotionLevel) {
            client.request(
                gql`
                    query GetPromotionLevelByName(
                        $project: String!,
                        $branch: String!,
                        $promotionLevel: String!,
                    ) {
                        promotionLevelByName(project: $project, branch: $branch, name: $promotionLevel) {
                            id
                            name
                            description
                            image
                        }
                    }
                `,
                {
                    project, branch, promotionLevel
                }
            ).then(data => {
                const pl = data.promotionLevelByName;
                setPromotionLevelObject(pl)
                setTitle(
                    <>
                        <Space size={4}>
                            Lead time to
                            <PromotionLevelImage promotionLevel={pl} />
                            <Typography.Text strong>{promotionLevel}</Typography.Text>
                            on {branch}@${project}
                            &nbsp;<ChartOptions interval={interval} period={period}/>
                        </Space>
                    </>
                )
            })
        }
    }, [client, project, branch, promotionLevel]);

    return (
        <>
            {
                promotionLevelObject &&
                <PromotionLevelTTRChart
                    promotionLevel={promotionLevelObject}
                    interval={interval}
                    period={period}
                />
            }
        </>
    )
}