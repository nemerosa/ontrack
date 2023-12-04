import {useContext} from "react";
import {Form, InputNumber} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectMultiplePromotionLevelNames from "@components/promotionLevels/SelectMultiplePromotionLevelNames";
import SelectLabel from "@components/labels/SelectLabel";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";

export default function ProjectPromotionWidgetForm({project, promotions, depth, label}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="project"
                    label="Project"
                    initialValue={project}
                    extra="Project for which to display the promotions"
                >
                    <SelectProject/>
                </Form.Item>
                <Form.Item
                    name="promotions"
                    label="Promotions"
                    initialValue={promotions}
                    extra="List of promotions to display as rows"
                >
                    <SelectMultiplePromotionLevelNames/>
                </Form.Item>
                <Form.Item
                    name="depth"
                    label="Depth"
                    initialValue={depth}
                    extra="How deep must the dependencies be collected (0 or nothing means that only the direct links are displayed)"
                >
                    <InputNumber
                        min={0}
                        max={4}
                    />
                </Form.Item>
                <Form.Item
                    name="label"
                    label="Label"
                    initialValue={label}
                    extra="Project label to restrict the list of dependencies"
                >
                    <SelectLabel/>
                </Form.Item>
            </Form>
        </>
    )
}