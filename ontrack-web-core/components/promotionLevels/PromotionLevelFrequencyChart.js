import {gql} from "graphql-request";
import DurationChart from "@components/charts/DurationChart";
import CountChart from "@components/charts/CountChart";

export default function PromotionLevelFrequencyChart({promotionLevel, interval = "3m", period = "1w"}) {

    return (
        <>
            <CountChart
                query={
                    gql`
                        query PromotionLevelFrequencyChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "promotion-level-frequency",
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