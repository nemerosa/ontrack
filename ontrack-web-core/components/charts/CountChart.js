import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Bar, CartesianGrid, ComposedChart, Legend, Tooltip, XAxis, YAxis} from "recharts";
import ChartContainer from "@components/charts/ChartContainer";

export default function CountChart({
                                       query,
                                       variables,
                                       yTickFormatter,
                                       legendFormatter = () => "Count ",
                                       domain,
                                   }) {

    const client = useGraphQLClient()

    const [chart, setChart] = useState({
        categories: []
    })
    const [dataPoints, setDataPoints] = useState([])

    useEffect(() => {
        if (client) {
            client.request(
                query,
                variables
            ).then(data => {
                const chart = data.getChart
                /**
                 *
                 * categories: [],
                 * dates: [],
                 * data: {
                 *     value: [],
                 * }
                 */
                setChart(chart)
                setDataPoints(
                    chart.dates.map((date, index) => {
                        return {
                            date,
                            value: chart.data[index],
                        }
                    })
                )
            })
        }
    }, [client, query, variables]);

    const [inactiveSeries, setInactiveSeries] = useState([])

    const legendClick = ({dataKey}) => {
        if (inactiveSeries.includes(dataKey)) {
            setInactiveSeries(inactiveSeries.filter(el => el !== dataKey));
        } else {
            setInactiveSeries(prev => [...prev, dataKey]);
        }
    }

    return (
        <>
            <ChartContainer>
                <ComposedChart
                    data={dataPoints}
                >
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="date" angle={-45} tickMargin={30} height={80} interval="preserveStart"/>
                    <YAxis tickFormatter={yTickFormatter} domain={domain}/>
                    <Tooltip/>
                    <Legend formatter={legendFormatter} onClick={legendClick} style={{cursor: 'pointer'}}/>
                    <Bar dataKey="value" fill="#6666aa" hide={inactiveSeries.includes('value')}/>
                </ComposedChart>
            </ChartContainer>
        </>
    )
}