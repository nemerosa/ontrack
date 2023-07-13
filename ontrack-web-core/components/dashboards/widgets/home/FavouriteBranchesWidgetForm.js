import {Form} from "antd";
import {useContext} from "react";
import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";
import SelectProject from "@components/projects/SelectProject";

export default function FavouriteBranchesWidgetForm({project}) {

    const {editionForm} = useContext(WidgetContext)

    return (
        <>
            <Form
                layout="vertical"
                form={editionForm}
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