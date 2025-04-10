import {useContext} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {Form} from "antd";
import SelectChartPeriod from "@components/charts/SelectChartPeriod";
import SelectChartInterval from "@components/charts/SelectChartInterval";
import SelectProjectBranchValidationStamp from "@components/promotionLevels/SelectProjectBranchValidationStamp";

export default function ValidationChartWidgetForm({project, branch, validationStamp, interval, period}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    const {onReceivingValuesHandler} = useContext(DashboardWidgetCellContext)

    const onFormValues = (values) => {
        // Flattening
        return {
            interval: values.interval,
            period: values.period,
            project: values.validationStamp.project,
            branch: values.validationStamp.branch,
            validationStamp: values.validationStamp.validationStamp,
        }
    }

    onReceivingValuesHandler(onFormValues)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="validationStamp"
                    label="Validation stamp"
                    initialValue={{project, branch, validationStamp}}
                >
                    <SelectProjectBranchValidationStamp/>
                </Form.Item>
                <Form.Item
                    name="interval"
                    label="Interval"
                    initialValue={interval}
                >
                    <SelectChartInterval/>
                </Form.Item>
                <Form.Item
                    name="period"
                    label="Period"
                    initialValue={period}
                >
                    <SelectChartPeriod/>
                </Form.Item>
            </Form>
        </>
    )
}