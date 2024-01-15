import {gql} from "graphql-request";
import PercentageChart from "@components/charts/PercentageChart";

export default function PromotionLevelStabilityChart({promotionLevel, interval = "3m", period = "1w"}) {

    return (
        <>
            <PercentageChart
                query={
                    gql`
                        query PromotionLevelStabilityChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "promotion-level-success-rate",
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
                        id: promotionLevel.id
                    },
                    interval,
                    period,
                }}
            />
        </>
    )
}