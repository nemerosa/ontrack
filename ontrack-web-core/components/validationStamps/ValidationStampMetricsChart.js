import {gql} from "graphql-request";
import PercentageChart from "@components/charts/PercentageChart";
import MetricsChart from "@components/charts/MetricsChart";

export default function ValidationStampMetricsChart({validationStamp, interval = "3m", period = "1w"}) {
    return (
        <>
            <MetricsChart
                query={
                    gql`
                        query ValidationStampStabilityChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "validation-stamp-metrics",
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