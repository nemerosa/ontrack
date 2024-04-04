import {gql} from "graphql-request";
import CountChart from "@components/charts/CountChart";

export default function ValidationStampFrequencyChart({validationStamp, interval = "3m", period = "1w"}) {
    return (
        <>
            <CountChart
                query={
                    gql`
                        query ValidationStampFrequencyChart($parameters: JSON!, $interval: String!, $period: String!) {
                            getChart(input: {
                                name: "validation-stamp-frequency",
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