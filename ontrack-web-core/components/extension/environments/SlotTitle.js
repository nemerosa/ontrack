import {Card, Space, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";
import LoadingInline from "@components/common/LoadingInline";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import Link from "next/link";
import {projectUri} from "@components/common/Links";
import {FaExternalLinkAlt} from "react-icons/fa";
import {projectEnvironmentsUri} from "@components/extension/environments/EnvironmentsLinksUtils";

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
                <Link href={projectEnvironmentsUri(slot.project)}
                      title={`Management of environment for the ${slot.project.name} project`}>
                    {slot.project.name}
                </Link>
                {
                    slot.qualifier &&
                    <Typography.Text>[{slot.qualifier}]</Typography.Text>
                }
                <Link href={projectUri(slot.project)} title={`Link to the ${slot.project.name} project page`}>
                    <FaExternalLinkAlt/>
                </Link>
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