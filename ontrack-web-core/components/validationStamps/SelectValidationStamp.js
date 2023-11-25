import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SelectValidationStamp({
                                                  branch, value, onChange, onValidationStampSelected,
                                                  useName = false,
                                                  allowClear = false,
                                                  multiple = false,
                                              }) {

    const client = useGraphQLClient()

    const [validationStamps, setValidationStamps] = useState([])
    const [options, setOptions] = useState([])

    useEffect(() => {
        if (client && branch) {
            client.request(
                gql`
                    query GetValidationStamps($branchId: Int!) {
                        branches(id: $branchId) {
                            validationStamps {
                                id
                                name
                                image
                                description
                                annotatedDescription
                                dataType {
                                    descriptor {
                                        id
                                    }
                                    config
                                }
                            }
                        }
                    }
                `,
                {branchId: branch.id}
            ).then(data => {
                setValidationStamps(data.branches[0].validationStamps)
                setOptions(data.branches[0].validationStamps.map(vs => {
                    return {
                        value: useName ? vs.name : vs.id,
                        label: <Space>
                            <ValidationStampImage validationStamp={vs}/>
                            <Typography.Text>{vs.name}</Typography.Text>
                        </Space>
                    }
                }))
            })
        }
    }, [client, branch]);

    const onLocalChange = (value) => {
        if (onChange) onChange(value)
        if (onValidationStampSelected) {
            const vs = validationStamps.find(it => {
                if (useName) {
                    return it.name === value
                } else {
                    return it.id === value
                }
            })
            onValidationStampSelected(vs)
        }
    }

    return (
        <Select
            options={options}
            value={value}
            onChange={onLocalChange}
            allowClear={allowClear}
            mode={multiple ? "multiple" : undefined}
        />
    )
}