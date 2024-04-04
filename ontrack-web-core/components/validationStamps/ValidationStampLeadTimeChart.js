import DurationChart from "@components/charts/DurationChart";
import {gql} from "graphql-request";

export default function ValidationStampLeadTimeChart({validationStamp, interval = "3m", period = "1w"}) {
    return (
        <>
            <DurationChart
                query={
                    gql`
                        query ValidationStampLeadTimeChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "validation-stamp-durations",
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