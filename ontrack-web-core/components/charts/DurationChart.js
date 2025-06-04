import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {formatSeconds} from "@components/common/Duration";
import {Bar, CartesianGrid, ComposedChart, Legend, Line, Tooltip, XAxis, YAxis} from "recharts";
import ChartContainer from "@components/charts/ChartContainer";
import {brand} from "@components/common/brand/Colors";

export default function DurationChart({query, variables}) {

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
                 *     mean: [],
                 *     percentile90: [],
                 *     maximum: [],
                 * }
                 */
                setChart(chart)
                setDataPoints(
                    chart.dates.map((date, index) => {
                        return {
                            date,
                            mean: chart.data.mean[index],
                            percentile90: chart.data.percentile90[index],
                            maximum: chart.data.maximum[index],
                        }
                    })
                )
            })
        }
    }, [client, query, variables]);

    const legendFormatter = (value, entry, index) => {
        return chart.categories[index]
    }

    const [inactiveSeries, setInactiveSeries] = useState([])

    const legendClick = ({dataKey}) => {
        if (inactiveSeries.includes(dataKey)) {
            setInactiveSeries(inactiveSeries.filter(el => el !== dataKey));
        } else {
            setInactiveSeries(prev => [...prev, dataKey]);
        }
    }

    const durationFormatter = (value) => {
        return formatSeconds(value, "-")
    }

    return (
        <>
            <ChartContainer>
                <ComposedChart
                    data={dataPoints}
                >
                    <CartesianGrid strokeDasharray="3 3"/>
                    <XAxis dataKey="date" angle={-45} tickMargin={30} height={80} interval="preserveStart"/>
                    <YAxis tickFormatter={durationFormatter}/>
                    <Tooltip formatter={durationFormatter}/>
                    <Legend formatter={legendFormatter} onClick={legendClick} style={{cursor: 'pointer'}}/>
                    <Bar dataKey="mean" fill={brand.colors.lilac} hide={inactiveSeries.includes('mean')}/>
                    <Line type="monotone" connectNulls={true} dataKey="percentile90" stroke={brand.colors.purple} hide={inactiveSeries.includes('percentile90')}/>
                    <Line type="monotone" connectNulls={true} dataKey="maximum" stroke={brand.colors.green} hide={inactiveSeries.includes('maximum')}/>
                </ComposedChart>
            </ChartContainer>
        </>
    )
}