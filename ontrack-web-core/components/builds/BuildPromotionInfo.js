import React, {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import GridCell from "@components/grid/GridCell";
import {gql} from "graphql-request";
import {Steps} from "antd";
import buildPromotionInfoItem from "@components/builds/BuildPromotionInfoItem";
import {useReloadState} from "@components/common/StateUtils";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";

export default function BuildPromotionInfo({build}) {

    const client = useGraphQLClient()

    const [reloadState, reload] = useReloadState()

    const [loading, setLoading] = useState(true)
    const [items, setItems] = useState([])
    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    ${gqlSlotData}
                    ${gqlSlotPipelineData}
                    fragment BuildPromotionInfoItemDataContent on BuildPromotionInfoItemData {
                        __typename
                        ... on SlotPipeline {
                            ...SlotPipelineData
                            slot {
                                ...SlotData
                            }
                        }
                        ... on EnvironmentBuildCount {
                            id
                            count
                            build {
                                id
                                name
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                            }
                        }
                        ... on PromotionLevel {
                            id
                            name
                            image
                            description
                        }
                        ... on PromotionRun {
                            id
                            creation {
                                time
                                user
                            }
                            description
                            annotatedDescription
                            authorizations {
                                name
                                action
                                authorized
                            }
                        }
                    }
                    query BuildPromotionInfo($buildId: Int!) {
                        build(id: $buildId) {
                            id
                            authorizations {
                                name
                                action
                                authorized
                            }
                            promotionInfo {
                                items {
                                    promotionLevel {
                                        id
                                        name
                                        image
                                    }
                                    data {
                                        ...BuildPromotionInfoItemDataContent
                                    }
                                }
                            }
                        }
                    }
                `,
                {buildId: build.id}
            ).then(data => {
                const localBuild = data.build
                const promotionInfo = data.build.promotionInfo
                // Mapping items to their components
                const itemList = []
                promotionInfo.items.forEach(item => {
                    itemList.push(
                        buildPromotionInfoItem({
                            item: item.data,
                            promotionLevel: item.promotionLevel,
                            build: localBuild,
                            onChange: reload,
                        })
                    )
                })
                itemList.reverse()
                setItems(itemList)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, build.id, reloadState])

    return (
        <>
            <GridCell id="promotions" title="Promotions" padding={true}>
                <Steps
                    items={items}
                    labelPlacement="vertical"
                    size="small"
                />
            </GridCell>
        </>
    )
}