import {Form, InputNumber} from "antd";
import {useContext} from "react";
import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";

export default function LastActiveBranchesWidgetForm({count}) {

    const {editionForm} = useContext(WidgetContext)

    return (
        <>
            <Form
                layout="vertical"
                form={editionForm}
            >
                <Form.Item
                    name="count"
                    label="Number of branches to display"
                    initialValue={count}
                >
                    <InputNumber min={1} max={100} step={1}/>
                </Form.Item>
            </Form>
        </>
    )
}