import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Card, Space, Typography} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";

export default function SlotEligibleBuild({slot, onStart}) {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [build, setBuild] = useState()
    const [loadedSlot, setLoadedSlot] = useState()

    useEffect(() => {
        if (client && slot) {
            setLoading(true)
            client.request(
                gql`
                    query SlotEligibleBuild($id: String!) {
                        slotById(id: $id) {
                            authorizations {
                                name
                                action
                                authorized
                            }
                            eligibleBuild {
                                ...SlotPipelineBuildData
                            }
                        }
                    }
                    ${gqlSlotPipelineBuildData}
                `,
                {
                    id: slot.id,
                }
            ).then(data => {
                setLoadedSlot(data.slotById)
                setBuild(data.slotById?.eligibleBuild)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, slot])

    return (
        <>
            <Card loading={loading}>
                {
                    build &&
                    <Space>
                        <BuildLink build={build}/>
                        <PromotionRuns promotionRuns={build.promotionRuns}/>
                        <Typography.Text>is eligible</Typography.Text>
                        {
                            isAuthorized(loadedSlot, "pipeline", "create") &&
                            <SlotPipelineCreateButton
                                slot={slot}
                                build={build}
                                onStart={onStart}
                            />
                        }
                    </Space>
                }
                {
                    !build && <Typography.Text type="secondary">No eligible build</Typography.Text>
                }
            </Card>
        </>
    )
}