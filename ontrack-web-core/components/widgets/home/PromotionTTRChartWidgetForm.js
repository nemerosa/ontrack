import {useContext, useEffect, useMemo} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {Form} from "antd";
import SelectProjectBranchPromotionLevel from "@components/promotionLevels/SelectProjectBranchPromotionLevel";

export default function PromotionTTRChartWidgetForm({project, branch, promotionLevel}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    const {onReceivingValuesHandler} = useContext(DashboardWidgetCellContext)

    const onFormValues = (values) => {
        // Flattening
        return {
            ...values,
            project: values.promotionLevel.project,
            branch: values.promotionLevel.branch,
            promotionLevel: values.promotionLevel.promotionLevel,
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
                    name="promotionLevel"
                    label="Promotion level"
                    initialValue={{project, branch, promotionLevel}}
                >
                    <SelectProjectBranchPromotionLevel/>
                </Form.Item>
            </Form>
        </>
    )
}