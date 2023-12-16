import {gql} from "graphql-request";
import DurationChart from "@components/charts/DurationChart";

export default function PromotionLevelLeadTimeChart({promotionLevel, interval = "3m", period = "1w"}) {

    return (
        <>
            <DurationChart
                query={
                    gql`
                        query PromotionLevelLeadTimeChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "promotion-level-lead-time",
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