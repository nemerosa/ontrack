import {Card, Space, Typography} from "antd";
import ProjectLink from "@components/projects/ProjectLink";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";
import LoadingInline from "@components/common/LoadingInline";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function SlotTitle({slot, showLastDeployed = false}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(false)
    const [build, setBuild] = useState()

    useEffect(() => {
        if (client && showLastDeployed) {
            setLoading(true)
            client.request(
                gql`
                    query SlotLastDeployed($id: String!) {
                        slotById(id: $id) {
                            lastDeployedPipeline {
                                build {
                                    ...SlotPipelineBuildData
                                }
                            }
                        }
                    }
                    ${gqlSlotPipelineBuildData}
                `,
                {id: slot.id}
            ).then(data => {
                setBuild(data.slotById?.lastDeployedPipeline?.build)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, showLastDeployed])

    return (
        <>
            <Space>
                <ProjectLink project={slot.project} text={
                    <Typography.Text strong>{slot.project.name}</Typography.Text>
                }/>
                {
                    slot.qualifier &&
                    <Typography.Text>[{slot.qualifier}]</Typography.Text>
                }
                {
                    showLastDeployed && <LoadingInline loading={loading}>
                        {
                            build && <Card size="small">
                                <Space>
                                    <BuildLink build={build}/>
                                    <PromotionRuns promotionRuns={build.promotionRuns}/>
                                </Space>
                            </Card>
                        }
                    </LoadingInline>
                }
            </Space>
        </>
    )
}