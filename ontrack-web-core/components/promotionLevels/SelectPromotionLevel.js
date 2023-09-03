import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {PromotionLevelImage} from "@components/common/Links";

export default function SelectPromotionLevel({branch, value, onChange}) {

    const [options, setOptions] = useState([])

    useEffect(() => {
        if (branch) {
            graphQLCall(
                gql`
                    query GetPromotionLevels($branchId: Int!) {
                        branches(id: $branchId) {
                            promotionLevels {
                                id
                                name
                                image
                                description
                                annotatedDescription
                            }
                        }
                    }
                `,
                {branchId: branch.id}
            ).then(data => {
                setOptions(data.branches[0].promotionLevels.map(pl => {
                    return {
                        value: pl.id,
                        label: <Space>
                            <PromotionLevelImage promotionLevel={pl}/>
                            <Typography.Text>{pl.name}</Typography.Text>
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