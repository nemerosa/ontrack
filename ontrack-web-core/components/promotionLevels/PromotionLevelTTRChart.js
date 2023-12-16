import {gql} from "graphql-request";
import DurationChart from "@components/charts/DurationChart";

export default function PromotionLevelTTRChart({promotionLevel, interval = "3m", period = "1w"}) {

    return (
        <>
            <DurationChart
                query={
                    gql`
                        query PromotionLevelTTRChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "promotion-level-ttr",
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