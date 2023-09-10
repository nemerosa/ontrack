import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";
import {useContext} from "react";
import {Form} from "antd";
import SelectProject from "@components/projects/SelectProject";
import SelectMultiplePromotionLevelNames from "@components/promotionLevels/SelectMultiplePromotionLevelNames";
import SelectLabel from "@components/labels/SelectLabel";

export default function ProjectPromotionWidgetForm({project, promotions, label}) {

    const {editionForm} = useContext(WidgetContext)

    return (
        <>
            <Form
                layout="vertical"
                form={editionForm}
            >
                <Form.Item
                    name="project"
                    label="Project"
                    initialValue={project}
                >
                    <SelectProject/>
                </Form.Item>
                <Form.Item
                    name="promotions"
                    label="Promotions"
                    initialValue={promotions}
                >
                    <SelectMultiplePromotionLevelNames/>
                </Form.Item>
                <Form.Item
                    name="label"
                    label="Label"
                    initialValue={label}
                >
                    <SelectLabel/>
                </Form.Item>
            </Form>
        </>
    )
}