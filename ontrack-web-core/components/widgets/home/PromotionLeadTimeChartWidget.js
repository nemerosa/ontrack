import {useContext, useEffect, useState} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import PromotionLevelLeadTimeChart from "@components/promotionLevels/PromotionLevelLeadTimeChart";

export default function PromotionLeadTimeChartWidget({project, branch, promotionLevel}) {

    const client = useGraphQLClient()

    const {setTitle, setExtra} = useContext(DashboardWidgetCellContext)

    useEffect(() => {
        if (project && branch && promotionLevel) {
            setTitle(`Lead time to promotion ${promotionLevel} on ${branch}@${project}`)
        }
    }, [project, branch, promotionLevel])

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
                setPromotionLevelObject(data.promotionLevelByName)
            })
        }
    }, [client, project, branch, promotionLevel]);

    return (
        <>
            {
                promotionLevelObject &&
                <PromotionLevelLeadTimeChart
                    promotionLevel={promotionLevelObject}
                />
            }
        </>
    )
}