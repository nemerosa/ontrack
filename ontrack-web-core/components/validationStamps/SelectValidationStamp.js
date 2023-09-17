import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {PromotionLevelImage} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export default function SelectValidationStamp({branch, value, onChange, onValidationStampSelected, useName = false}) {

    const [validationStamps, setValidationStamps] = useState([])
    const [options, setOptions] = useState([])

    useEffect(() => {
        if (branch) {
            graphQLCall(
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
    }, [branch]);

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
        />
    )
}