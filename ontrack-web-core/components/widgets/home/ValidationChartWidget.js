import {useContext, useEffect} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {useValidationStampByName} from "@components/widgets/home/ValidationChartUtils";
import ValidationChartTitle from "@components/widgets/home/ValidationChartTitle";
import ChartTargetNotFound from "@components/widgets/home/ChartTargetNotFound";

/**
 * The frame shared by the two validation chart widgets: stability and metrics.
 *
 * The validation counterpart of `PromotionChartWidget`, with the same three states: loading with
 * the configured names in the title, loaded with the chart, and not found with the reason in the
 * body (#1694).
 *
 * @param chart The chart component, receiving `validationStamp`, `interval` and `period`
 */
export default function ValidationChartWidget({
                                                  prefix,
                                                  chart: Chart,
                                                  project,
                                                  branch,
                                                  validationStamp,
                                                  interval,
                                                  period,
                                              }) {

    const {setTitle} = useContext(DashboardWidgetCellContext)

    const {validationStampObject, notFound} = useValidationStampByName(project, branch, validationStamp)

    useEffect(() => {
        setTitle(
            <ValidationChartTitle
                prefix={prefix}
                project={project}
                branch={branch}
                validationStampName={validationStamp}
                validationStamp={validationStampObject}
                notFound={notFound}
                interval={interval}
                period={period}
            />
        )
    }, [setTitle, prefix, project, branch, validationStamp, validationStampObject, notFound, interval, period]);

    return (
        <>
            {
                validationStampObject &&
                <Chart
                    validationStamp={validationStampObject}
                    interval={interval}
                    period={period}
                />
            }
            {
                notFound &&
                <ChartTargetNotFound
                    entity="Validation stamp"
                    name={validationStamp}
                    project={project}
                    branch={branch}
                />
            }
        </>
    )
}
