import GridCellCommand from "@components/grid/GridCellCommand";
import ChartOptions from "@components/charts/ChartOptions";
import {FaCog} from "react-icons/fa";

export default function ChartOptionsCommand({interval, period, onClick}) {
    return (
        <>
            <GridCellCommand
                title="Configuration of the interval and period"
                icon={<FaCog/>}
                onAction={onClick}
            >
                <ChartOptions interval={interval} period={period}></ChartOptions>
            </GridCellCommand>
        </>
    )
}