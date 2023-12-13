import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Bar, BarChart, CartesianGrid, Legend, Rectangle, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";

export default function PromotionLevelLeadTimeChart({promotionLevel}) {

    const client = useGraphQLClient()

    const [meanData, setMeanData] = useState([])

    useEffect(() => {
        if (client && promotionLevel) {
            client.request(
                gql`
                    query PromotionLevelLeadTimeChart($parameters: JSON!) {
                        getChart(input: {
                            name: "promotion-level-lead-time",
                            options: {
                                interval: "1y",
                                period: "1w",
                            },
                            parameters: $parameters,
                        })
                    }
                `,
                {
                    parameters: {
                        id: promotionLevel.id,
                    }
                }
            ).then(data => {
                const chart = data.getChart
                /**
                 *
                 * categories: [],
                 * dates: [],
                 * data: {
                 *     mean: [],
                 *     percentile90: [],
                 *     maximum: [],
                 * }
                 */
                setMeanData(
                    chart.dates.map((date, index) => {
                        return {
                            date,
                            value: chart.data.mean[index],
                        }
                    })
                )
            })
        }
    }, [client, promotionLevel]);

    return (
        <>
            <ResponsiveContainer width="100%" height="100%">
                <BarChart
                    data={meanData}
                >
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="date" angle={-45} tickMargin={30} height={80} interval="preserveStart"/>
                    <YAxis/>
                    <Tooltip/>
                    <Legend/>
                    <Bar dataKey="value" fill="#8884d8"/>
                </BarChart>
            </ResponsiveContainer>
        </>
    )
}