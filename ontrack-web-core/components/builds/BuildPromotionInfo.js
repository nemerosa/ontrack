import React, {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import GridCell from "@components/grid/GridCell";
import {gql} from "graphql-request";
import {Timeline} from "antd";
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
                    fragment BuildPromotionInfoItemData on BuildPromotionInfoItem {
                        __typename
                        ... on Slot {
                            id
                            environment {
                                id
                                name
                            }
                            project {
                                id
                                name
                            }
                            qualifier
                        }
                        ... on SlotPipeline {
                            ...SlotPipelineData
                            slot {
                                ...SlotData
                            }
                        }
                        ... on PromotionLevel {
                            id
                            name
                            image
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
                                noPromotionItems {
                                    ...BuildPromotionInfoItemData
                                }
                                withPromotionItems {
                                    promotionLevel {
                                        id
                                        name
                                        image
                                    }
                                    items {
                                        ...BuildPromotionInfoItemData
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
                // First, the items not linked to any promotion
                promotionInfo.noPromotionItems.forEach(item => {
                    itemList.push(
                        buildPromotionInfoItem({
                            item,
                            build: localBuild,
                            onChange: reload,
                        })
                    )
                })
                // Then, promotions and their items
                promotionInfo.withPromotionItems.forEach(({promotionLevel, items}) => {
                    items.forEach(item => {
                        itemList.push(
                            buildPromotionInfoItem({
                                item,
                                build: localBuild,
                                promotionLevel,
                                onChange: reload,
                            })
                        )
                    })
                })
                // OK
                setItems(itemList)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, build.id, reloadState])

    return (
        <>
            <GridCell id="promotions" title="Promotions" padding={true}>
                <Timeline
                    loading={loading}
                    mode="left"
                    items={items}
                />
            </GridCell>
        </>
    )
}