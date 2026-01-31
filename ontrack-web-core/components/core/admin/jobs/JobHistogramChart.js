import {Bar, BarChart, Cell, ResponsiveContainer, Tooltip, XAxis} from "recharts";
import {useEffect, useState} from "react";
import dayjs from "dayjs";

export default function JobHistogramChart({histogram}) {

    const [dataPoints, setDataPoints] = useState([])
    useEffect(() => {
        setDataPoints(
            histogram.items.map(item => {
                return {
                    date: item.from,
                    value: item.avgDurationMs,
                    error: item.error,
                }
            })
        )
    }, [histogram])

    return (
        <>
            <ResponsiveContainer style={{
                width: '145px',
                maxWidth: '145px',
                aspectRatio: 4,
                borderBottom: "solid 1px #ccc",
            }}>
                <BarChart
                    data={dataPoints}
                >
                    <XAxis dataKey="date" hide={true}/>
                    <Tooltip labelFormatter={(value) => dayjs(value).format('YYYY-MM-DD')}/>
                    <Bar name="Duration (avg ms)" dataKey="value">
                        {
                            dataPoints.map((entry, index) => (
                                <Cell key={`cell-${index}`} fill={entry.error ? '#CC3333' : '#33CC33'}/>
                            ))
                        }
                    </Bar>
                </BarChart>
            </ResponsiveContainer>
        </>
    )
}