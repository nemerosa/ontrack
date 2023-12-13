import {gql} from "graphql-request";
import DurationChart from "@components/charts/DurationChart";

export default function PromotionLevelLeadTimeChart({promotionLevel}) {

    return (
        <>
            <DurationChart
                query={
                    gql`
                        query PromotionLevelLeadTimeChart($parameters: JSON!) {
                            getChart(input: {
                                name: "promotion-level-lead-time",
                                options: {
                                    interval: "3m",
                                    period: "1w",
                                },
                                parameters: $parameters,
                            })
                        }
                    `
                }
                variables={{
                    parameters: {
                        id: promotionLevel.id
                    }
                }}
            />
        </>
    )
}