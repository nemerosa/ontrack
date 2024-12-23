import {useContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlSlotData, gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import {Card, Flex, Space} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import SlotBuildEligibilitySwitch from "@components/extension/environments/SlotBuildEligibilitySwitch";
import {EventsContext} from "@components/common/EventsContext";

export default function ProjectEnvironmentsBuilds({projectName, slotId, qualifier = ""}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [showAllBuilds, setShowAllBuilds] = useState(false)
    const [builds, setBuilds] = useState([])

    const [selectedBuild, setSelectedBuild] = useState()

    useEffect(() => {
        if (client) {

            const loadEligibleBuilds = async () => {
                const data = await client.request(
                    gql`
                        ${gqlSlotPipelineData}
                        ${gqlSlotData}
                        query SlotBuilds(
                            $slotId: String!,
                            $qualifier: String!,
                            $deployable: Boolean!,
                        ) {
                            slotById(id: $slotId) {
                                eligibleBuilds(deployable: $deployable, size: 10) {
                                    pageItems {
                                        ...SlotPipelineBuildData
                                        deployed: currentDeployments(qualifier: $qualifier) {
                                            ...SlotPipelineData
                                            slot {
                                                ...SlotData
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    `,
                    {
                        slotId,
                        qualifier,
                        deployable: !showAllBuilds,
                    }
                )
                setBuilds(data.slotById.eligibleBuilds.pageItems)
            }

            const loadLastBuilds = async () => {
                const data = await client.request(
                    gql`
                        ${gqlSlotPipelineData}
                        ${gqlSlotData}
                        query ProjectLastBuilds(
                            $projectName: String!,
                            $qualifier: String!,
                        ) {
                            builds(project: $projectName, buildProjectFilter: {maximumCount: 10}) {
                                ...SlotPipelineBuildData
                                deployed: currentDeployments(qualifier: $qualifier) {
                                    ...SlotPipelineData
                                    slot {
                                        ...SlotData
                                    }
                                }
                            }
                        }
                    `,
                    {
                        projectName,
                        qualifier,
                    }
                )
                setBuilds(data.builds)
            }

            const loadBuilds = async () => {
                if (slotId) {
                    await loadEligibleBuilds()
                } else {
                    await loadLastBuilds()
                }
            }

            setLoading(true)
            loadBuilds().finally(() => setLoading(false))
        }
    }, [client, projectName, slotId, qualifier, showAllBuilds])

    const eventsContext = useContext(EventsContext)

    const onBuildClick = (build) => {
        return () => {
            if (build.id === selectedBuild?.id) {
                setSelectedBuild(null)
                eventsContext.fireEvent("build.selected", {id: ''})
            } else {
                setSelectedBuild(build)
                eventsContext.fireEvent("build.selected", {id: build.id})
            }
        }
    }

    return (
        <>
            <Card
                size="small"
                style={{
                    height: "100%",
                }}
                loading={loading}
            >
                <Space direction="vertical">
                    {
                        slotId && <SlotBuildEligibilitySwitch value={showAllBuilds} onChange={setShowAllBuilds}/>
                    }
                    <Flex
                        justify="flex-start"
                        alignItems="flex-start"
                        gap="small"
                    >
                        {
                            builds.map(build => (
                                <Card
                                    size="small"
                                    key={build.id}
                                    hoverable={true}
                                    onClick={onBuildClick(build)}
                                    style={{
                                        border: build.id === selectedBuild?.id ? "solid 3px black" : undefined,
                                    }}
                                >
                                    <Flex
                                        vertical={true}
                                    >
                                        <Flex
                                            justify="flex-start"
                                            alignItems="flex-start"
                                            gap="small"
                                        >
                                            <Flex
                                                justify="flex-start"
                                                alignItems="flex-start"
                                                gap="small"
                                                vertical={true}
                                            >
                                                {
                                                    build.deployed.map(pipeline => (
                                                        <>
                                                            <EnvironmentIcon
                                                                environmentId={pipeline.slot.environment.id}
                                                                tooltipText={`Deployed in ${pipeline.slot.environment.name}`}
                                                            />
                                                        </>
                                                    ))
                                                }
                                            </Flex>
                                            <Flex
                                                justify="flex-start"
                                                alignItems="flex-start"
                                                gap="small"
                                                vertical={true}
                                            >
                                                <BuildLink build={build}/>
                                                <PromotionRuns promotionRuns={build.promotionRuns}/>
                                            </Flex>
                                        </Flex>
                                        {/*<Flex*/}
                                        {/*    justify="center"*/}
                                        {/*    alignItems="center"*/}
                                        {/*    style={{*/}
                                        {/*        paddingTop: "0.5em",*/}
                                        {/*        marginTop: "auto",*/}
                                        {/*    }}*/}
                                        {/*>*/}
                                        {/*    <Button>*/}
                                        {/*        <FaAngleDoubleDown/>*/}
                                        {/*    </Button>*/}
                                        {/*</Flex>*/}
                                    </Flex>
                                </Card>
                            ))
                        }
                    </Flex>
                </Space>
            </Card>
        </>
    )
}