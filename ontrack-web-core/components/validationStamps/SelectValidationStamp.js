import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {PromotionLevelImage} from "@components/common/Links";
import ValidationStampImage from "@components/validationStamps/ValidationStampImage";

export default function SelectValidationStamp({branch, value, onChange, useName = false}) {

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

    return (
        <Select
            options={options}
            value={value}
            onChange={onChange}
        />
    )
}