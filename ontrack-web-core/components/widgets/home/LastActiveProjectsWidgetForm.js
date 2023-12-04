import {Form, InputNumber} from "antd";
import {useContext} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";

export default function LastActiveProjectsWidgetForm({count}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="count"
                    label="Number of projects to display"
                    initialValue={count}
                >
                    <InputNumber min={1} max={100} step={1}/>
                </Form.Item>
            </Form>
        </>
    )
}