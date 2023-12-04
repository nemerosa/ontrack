import {Form} from "antd";
import {useContext} from "react";
import SelectProject from "@components/projects/SelectProject";
import {DashboardWidgetCellContext} from "@components/dashboards/DashboardWidgetCellContextProvider";

export default function FavouriteBranchesWidgetForm({project}) {

    const {widgetEditionForm} = useContext(DashboardWidgetCellContext)

    return (
        <>
            <Form
                layout="vertical"
                form={widgetEditionForm}
            >
                <Form.Item
                    name="project"
                    label="Branches are restricted to this project"
                    initialValue={project}
                >
                    <SelectProject/>
                </Form.Item>
            </Form>
        </>
    )
}