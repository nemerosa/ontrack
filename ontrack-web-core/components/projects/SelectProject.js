import {useState} from "react";
import {Select} from "antd";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function SelectProject({value, onChange}) {
    const [projects, setProjects] = useState([])
    const [searching, setSearching] = useState(false)

    const handleChange = (newValue) => {
        if (onChange) onChange(newValue)
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
        if (onChange) onChange(null)
    }

    return (
        <>
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
                style={{
                    width: '15em',
                }}
            />
        </>
    )
}