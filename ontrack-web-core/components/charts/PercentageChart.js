import CountChart from "@components/charts/CountChart";

export default function PercentageChart({query, variables}) {
    const percentageFormatter = (value) => `${value}%`

    return (
        <>
            <CountChart
                query={query}
                variables={variables}
                yTickFormatter={percentageFormatter}
                legendFormatter={() => "%"}
                domain={[0, 100]}
            />
        </>
    )
}