import {useState} from "react";
import {Select} from "antd";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SelectProject({
                                          value, onChange,
                                          placeholder = "Project name",
                                          idAsValue = false,
                                      }) {

    const client = useGraphQLClient()

    const [projects, setProjects] = useState([])
    const [searching, setSearching] = useState(false)

    const handleChange = (newValue) => {
        if (onChange) onChange(newValue)
    }

    const handleSearch = (token) => {
        if (token && token.length > 2) {
            if (!searching) {
                setSearching(true)
                client.request(
                    gql`
                        query SearchProjects($token: String!) {
                            projects(pattern: $token) {
                                id
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
                placeholder={placeholder}
                defaultActiveFirstOption={true}
                suffixIcon={null}
                filterOption={false}
                onSearch={handleSearch}
                onChange={handleChange}
                allowClear={true}
                onClear={handleClear}
                notFoundContent={null}
                options={(projects || []).map((d) => ({
                    value: idAsValue ? d.id : d.name,
                    label: d.name,
                }))}
                style={{
                    width: '16em',
                }}
            />
        </>
    )
}