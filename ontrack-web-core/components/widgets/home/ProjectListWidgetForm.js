import {useContext} from "react";
import {Form} from "antd";
import SelectProject from "@components/projects/SelectProject";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";

export default function ProjectListWidgetForm({projectNames}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="projectNames"
                    label="Projects"
                    initialValue={projectNames}
                    extra="List of projects to display"
                >
                    <SelectProject multiple={true} width="100%"/>
                </Form.Item>
            </Form>
        </>
    )
}