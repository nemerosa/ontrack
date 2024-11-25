import {Form, Input, Select} from "antd";
import {useContext} from "react";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";
import SelectProject from "@components/projects/SelectProject";

export default function EnvironmentListWidgetForm({title, tags, projects}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="title"
                    label="Title"
                    initialValue={title}
                >
                    <Input/>
                </Form.Item>
                <Form.Item
                    name="tags"
                    label="Tags"
                    initialValue={tags}
                >
                    <Select
                        mode="tags"
                    />
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