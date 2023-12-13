import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";

export default function PromotionLeadTimeChartWidget({project, branch, promotionLevel}) {

    const {setTitle, setExtra} = useContext(DashboardWidgetCellContext)

    useEffect(() => {
        if (project && branch && promotionLevel) {
            setTitle(`Lead time to promotion ${promotionLevel} on ${branch}@${project}`)
        }
    }, [project, branch, promotionLevel]);

    return (
        <>
            W: {JSON.stringify({project, branch, promotionLevel})}
        </>
    )
}