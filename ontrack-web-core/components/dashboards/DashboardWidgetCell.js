import DashboardWidgetCellContextProvider from "@components/dashboards/DashboardWidgetCellContextProvider";
import DashboardWidgetCellWrapper from "@components/dashboards/DashboardWidgetCellWrapper";

export default function DashboardWidgetCell({widget}) {
    return (
        <>
            <DashboardWidgetCellContextProvider widget={widget}>
                <DashboardWidgetCellWrapper widget={widget}/>
            </DashboardWidgetCellContextProvider>
        </>
    )
}