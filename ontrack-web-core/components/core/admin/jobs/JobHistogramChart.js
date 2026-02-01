import {Bar, BarChart, Cell, ResponsiveContainer, Tooltip, XAxis} from "recharts";
import {useEffect, useState} from "react";
import dayjs from "dayjs";

function formatDuration(durationMs) {
    if (durationMs < 1000) {
        return `${durationMs} ms`
    } else {
        return dayjs.duration(durationMs, 'milliseconds').format('H[h] m[m] s[s]')
    }
}

const JobHistogramTooltip = ({active, payload}) => {
    if (active && payload && payload.length) {
        const {date, displayValue, count, errorCount} = payload[0].payload;
        if (count > 0) {
            return (
                <div style={{
                    backgroundColor: 'white',
                    border: '1px solid #ccc',
                    padding: '5px',
                }}>
                    <div>{dayjs(date).format('YYYY-MM-DD')}</div>
                    <div>
                        Duration (avg): {displayValue}
                    </div>
                    <div>
                        Measures: {count}
                    </div>
                    <div>
                        Errors: {errorCount}
                    </div>
                </div>
            )
        } else {
            return <div style={{
                backgroundColor: 'white',
                border: '1px solid #ccc',
                padding: '5px',
                fontStyle: 'italic',
            }}>
                No measures
            </div>
        }
    }
    return null;
}

export default function JobHistogramChart({histogram}) {

    const [dataPoints, setDataPoints] = useState([])
    useEffect(() => {
        setDataPoints(
            histogram.items.map(item => {
                return {
                    date: item.from,
                    value: item.avgDurationMs,
                    displayValue: formatDuration(item.avgDurationMs),
                    count: item.count,
                    errorCount: item.errorCount,
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
                    <Tooltip content={<JobHistogramTooltip/>}/>
                    <Bar name="Duration (avg)" dataKey="value">
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