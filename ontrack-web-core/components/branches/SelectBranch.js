import {useState} from "react";
import {Select} from "antd";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function SelectBranch({
                                         project, value, onChange, disabled,
                                         placeholder = "Type the name of the branch",
                                         idAsValue = false,
                                     }) {
    const [branches, setBranches] = useState([])
    const [searching, setSearching] = useState(false)

    const handleChange = (newValue) => {
        if (onChange) onChange(newValue)
    }

    const handleSearch = (token) => {
        if (project && token && token.length > 2) {
            if (!searching) {
                setSearching(true)
                graphQLCall(
                    gql`
                        query SearchBranches($project: String!, $token: String!) {
                            branches(project: $project, token: $token) {
                                id
                                name
                            }
                        }
                    `, {project, token}
                ).then(data => {
                    setBranches(data.branches)
                }).finally(() => {
                    setSearching(false)
                })
            }
        } else {
            setBranches([])
        }
    }

    const handleClear = () => {
        setBranches([])
        if (onChange) onChange(null)
    }

    return (
        <>
            <Select
                disabled={disabled}
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
                options={(branches || []).map((d) => ({
                    value: idAsValue ? d.id : d.name,
                    label: d.name,
                }))}
                style={{
                    width: '15em',
                }}
            />
        </>
    )
}