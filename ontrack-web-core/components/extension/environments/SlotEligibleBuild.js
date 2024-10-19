import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import LoadingInline from "@components/common/LoadingInline";
import {Space, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function SlotEligibleBuild({slot}) {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [build, setBuild] = useState()

    useEffect(() => {
        if (client && slot) {
            setLoading(true)
            client.request(
                gql`
                    query SlotEligibleBuild($id: String!) {
                        slotById(id: $id) {
                            eligibleBuild {
                                id
                                name
                                creation {
                                    time
                                }
                                branch {
                                    id
                                    name
                                    project {
                                        id
                                        name
                                    }
                                }
                                promotionRuns(lastPerLevel: true) {
                                    id
                                    creation {
                                        time
                                    }
                                    promotionLevel {
                                        id
                                        name
                                        description
                                        image
                                        _image
                                    }
                                }
                                releaseProperty {
                                    value
                                }
                            }
                        }
                    }
                `,
                {
                    id: slot.id,
                }
            ).then(data => {
                setBuild(data.slotById?.eligibleBuild)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, slot])

    return (
        <>
            <LoadingInline loading={loading}>
                {
                    build &&
                    <Space>
                        <BuildLink build={build}/>
                        <PromotionRuns promotionRuns={build.promotionRuns}/>
                        <Typography.Text>is eligible</Typography.Text>
                    </Space>
                }
                {
                    !build && <Typography.Text type="secondary">No eligible build</Typography.Text>
                }
            </LoadingInline>
        </>
    )
}