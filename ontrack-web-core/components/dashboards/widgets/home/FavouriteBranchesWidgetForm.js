import {Form, InputNumber, Select} from "antd";
import {useContext, useEffect, useState} from "react";
import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function FavouriteBranchesWidgetForm({project}) {

    const {editionForm} = useContext(WidgetContext)

    const [projects, setProjects] = useState([])
    const [value, setValue] = useState(null)
    const [searching, setSearching] = useState(false)

    const handleChange = (newValue) => {
        setValue(newValue);
    }

    const handleSearch = (token) => {
        if (token && token.length > 2) {
            if (!searching) {
                setSearching(true)
                graphQLCall(
                    gql`
                        query SearchProjects($token: String!) {
                            projects(pattern: $token) {
                                name
                            }
                        }
                    `, {token}
                ).then(data => {
                    setProjects(data.projects)
                }).finally(() => {
                    setSearching(false)
                })
            }
        } else {
            setProjects([])
        }
    }

    const handleClear = () => {
        setProjects([])
        setValue(null)
    }

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
                        showSearch={true}
                        value={value}
                        placeholder="Type the name of the project"
                        defaultActiveFirstOption={true}
                        showArrow={false}
                        filterOption={false}
                        onSearch={handleSearch}
                        onChange={handleChange}
                        allowClear={true}
                        onClear={handleClear}
                        notFoundContent={null}
                        options={(projects || []).map((d) => ({
                            value: d.name,
                            label: d.name,
                        }))}
                    />
                </Form.Item>
            </Form>
        </>
    )
}