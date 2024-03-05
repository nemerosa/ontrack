import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {Bar, CartesianGrid, ComposedChart, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis} from "recharts";

export default function CountChart({query, variables, yTickFormatter}) {

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

    const legendFormatter = (value, entry, index) => {
        return "Count"
    }

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
            <ResponsiveContainer width="100%" height="100%">
                <ComposedChart
                    data={dataPoints}
                >
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="date" angle={-45} tickMargin={30} height={80} interval="preserveStart"/>
                    <YAxis tickFormatter={yTickFormatter}/>
                    <Tooltip/>
                    <Legend formatter={legendFormatter} onClick={legendClick} style={{cursor: 'pointer'}}/>
                    <Bar dataKey="value" fill="#6666aa" hide={inactiveSeries.includes('value')}/>
                </ComposedChart>
            </ResponsiveContainer>
        </>
    )
}