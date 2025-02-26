import {useContext} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import {Form, Input, Switch} from "antd";
import SelectMultipleValidationStampsNames from "@components/validationStamps/SelectMultipleValidationStampsNames";
import SelectProjectBranch from "@components/branches/SelectProjectBranch";

export default function LastValidationsForBranchWidgetForm({title, project, branch, validations, displayPromotions}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    const {onReceivingValuesHandler} = useContext(DashboardWidgetCellContext)

    const onFormValues = (values) => {
        console.log({LastValidationsForBranchWidgetForm: values})
        return {
            title: values.title,
            validations: values.validations,
            displayPromotions: values.displayPromotions,
            project: values.selectedBranch.project,
            branch: values.selectedBranch.branch,
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
                    name="title"
                    label="Title"
                    extra="If not provided, a default title is displayed, based on the selected elements."
                    initialValue={title}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="selectedBranch"
                    label="Branch"
                    initialValue={{project, branch}}
                >
                    <SelectProjectBranch/>
                </Form.Item>
                <Form.Item
                    name="validations"
                    label="List of validations to display"
                    initialValue={validations}
                >
                    <SelectMultipleValidationStampsNames/>
                </Form.Item>
                <Form.Item
                    name="displayPromotions"
                    label="Display promotions"
                    extra="If checked, displays the promotions for each build"
                    initialValue={displayPromotions}
                >
                    <Switch/>
                </Form.Item>
            </Form>
        </>
    )
}