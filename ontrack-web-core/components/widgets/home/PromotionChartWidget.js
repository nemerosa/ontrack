import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {usePromotionLevel} from "@components/widgets/home/promotionChartUtils";
import PromotionChartTitle from "@components/widgets/home/PromotionChartTitle";
import ChartTargetNotFound from "@components/widgets/home/ChartTargetNotFound";
import ChartTargetError from "@components/widgets/home/ChartTargetError";

/**
 * The frame shared by the four promotion chart widgets: lead time, frequency, stability and TTR.
 *
 * Each widget is the same story with a different `prefix` and `chart`: resolve the configured
 * promotion level, caption the cell, and draw the chart once the level is loaded. The title is set
 * from the first render, so the cell never shows an empty entity while the level loads, and a level
 * which does not exist is named as not found in the title and explained in the body rather than
 * leaving the widget captioned "Loading..." for good (#1694).
 *
 * @param chart The chart component, receiving `promotionLevel`, `interval` and `period`
 */
export default function PromotionChartWidget({
                                                 prefix,
                                                 chart: Chart,
                                                 project,
                                                 branch,
                                                 promotionLevel,
                                                 interval,
                                                 period,
                                             }) {

    const {setTitle} = useContext(DashboardWidgetCellContext)

    const {promotionLevelObject, notFound, error} = usePromotionLevel(project, branch, promotionLevel)

    useEffect(() => {
        setTitle(
            <PromotionChartTitle
                prefix={prefix}
                project={project}
                branch={branch}
                promotionLevelName={promotionLevel}
                promotionLevel={promotionLevelObject}
                notFound={notFound}
                interval={interval}
                period={period}
            />
        )
    }, [setTitle, prefix, project, branch, promotionLevel, promotionLevelObject, notFound, interval, period]);

    return (
        <>
            {
                promotionLevelObject &&
                <Chart
                    promotionLevel={promotionLevelObject}
                    interval={interval}
                    period={period}
                />
            }
            {
                error &&
                <ChartTargetError error={error}/>
            }
            {
                notFound &&
                <ChartTargetNotFound
                    entity="Promotion level"
                    name={promotionLevel}
                    project={project}
                    branch={branch}
                />
            }
        </>
    )
}
