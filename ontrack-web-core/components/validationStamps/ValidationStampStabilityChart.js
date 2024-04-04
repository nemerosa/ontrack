import {gql} from "graphql-request";
import PercentageChart from "@components/charts/PercentageChart";

export default function ValidationStampStabilityChart({validationStamp, interval = "3m", period = "1w"}) {
    return (
        <>
            <PercentageChart
                query={
                    gql`
                        query ValidationStampStabilityChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "validation-stamp-stability",
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
                        id: validationStamp.id
                    },
                    interval,
                    period,
                }}
            />
        </>
    )
}