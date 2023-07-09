import {Form, InputNumber, Select} from "antd";
import {useContext, useEffect, useState} from "react";
import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function FavouriteBranchesWidgetForm({project}) {

    const {editionForm} = useContext(WidgetContext)

    const [projects, setProjects] = useState([])
    useEffect(() => {
        graphQLCall(
            gql`
                query Projects {
                    projects {
                        value: name
                        label: name
                    }
                }
            `
        ).then(data => {
            setProjects(data.projects)
        })
    }, [])

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
                    <Select
                        options={projects}
                    />
                </Form.Item>
            </Form>
        </>
    )
}