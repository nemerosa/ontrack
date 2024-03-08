import {ResponsiveContainer} from "recharts";

export default function ChartContainer({children}) {
    return (
        <>
            <ResponsiveContainer width="100%" height="98%">
                {children}
            </ResponsiveContainer>
        </>
    )
}