import {Form} from "antd";
import {useContext} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import SelectProject from "@components/projects/SelectProject";
import SelectEnvironmentName from "@components/extension/environments/SelectEnvironmentName";

export default function EnvironmentWidgetForm({name, projects}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="name"
                    label="Environment name"
                    initialValue={name}
                >
                    <SelectEnvironmentName/>
                </Form.Item>
                <Form.Item
                    name="projects"
                    label="Projects"
                    initialValue={projects}
                >
                    <SelectProject
                        multiple={true}
                        width="100%"
                        placeholder="Project names"
                    />
                </Form.Item>
            </Form>
        </>
    )
}