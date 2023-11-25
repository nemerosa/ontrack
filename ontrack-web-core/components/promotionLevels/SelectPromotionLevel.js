import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {PromotionLevelImage} from "@components/common/Links";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SelectPromotionLevel({branch, value, onChange, useName = false, allowClear = false}) {

    const client = useGraphQLClient()

    const [options, setOptions] = useState([])

    useEffect(() => {
        if (branch && client) {
            client.request(
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
                        value: useName ? pl.name : pl.id,
                        label: <Space>
                            <PromotionLevelImage promotionLevel={pl}/>
                            <Typography.Text>{pl.name}</Typography.Text>
                        </Space>
                    }
                }))
            })
        }
    }, [client, branch]);

    return (
        <Select
            options={options}
            value={value}
            onChange={onChange}
            allowClear={allowClear}
        />
    )
}