import {gql} from "graphql-request";
import DurationChart from "@components/charts/DurationChart";

export default function E2ELeadTimeChart({promotionLevel, targetPromotionLevel, interval = "3m", period = "1w"}) {

    return (
        <>
            {
                targetPromotionLevel.branch &&
                <DurationChart
                    query={
                        gql`
                            query E2ELeadTimeChart($parameters: JSON!, $interval: String!, $period: String!) {
                                getChart(input: {
                                    name: "e2e-lead-time",
                                    options: {
                                        interval: $interval,
                                        period: $period,
                                    },
                                    parameters: $parameters,
                                })
                            }
                        `
                    }
                    variables={{
                        parameters: {
                            refPromotionId: promotionLevel.id,
                            samePromotion: promotionLevel.name === targetPromotionLevel.name,
                            targetPromotionId: promotionLevel.name === targetPromotionLevel.name ? null : targetPromotionLevel.id,
                            targetProject: targetPromotionLevel.branch.project.name,
                        },
                        interval,
                        period,
                    }}
                />
            }
        </>
    )
}
