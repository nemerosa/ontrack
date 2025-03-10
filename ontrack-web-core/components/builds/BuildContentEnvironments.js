import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {
    gqlSlotData,
    gqlSlotPipelineBuildData,
    gqlSlotPipelineDataNoBuild
} from "@components/extension/environments/EnvironmentGraphQL";
import {Flex, List, Typography} from "antd";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import {useRefresh} from "@components/common/RefreshUtils";
import BuildDeploymentListItem from "@components/builds/environments/BuildDeploymentListItem";
import BuildSlotInfo from "@components/builds/environments/BuildSlotInfo";

export default function BuildContentEnvironments({build}) {

    const [refreshState, refresh] = useRefresh()

    const {data, loading} = useQuery(
        gql`
            ${gqlSlotPipelineDataNoBuild}
            ${gqlSlotData}
            ${gqlSlotPipelineBuildData}
            query BuildEnvironments(
                $id: Int!,
            ) {
                build(id: $id) {
                    slots {
                        ...SlotData
                        lastDeployedPipeline {
                            build {
                                ...SlotPipelineBuildData
                            }
                        }
                        currentPipeline {
                            ...SlotPipelineDataNoBuild
                            build {
                                ...SlotPipelineBuildData
                            }
                        }
                        pipelines(buildId: $id, size: 2) {
                            pageItems {
                                ...SlotPipelineDataNoBuild
                            }
                        }
                        authorizations {
                            name
                            action
                            authorized
                        }
                    }
                }
            }
        `,
        {
            variables: {id: build.id},
            deps: [refreshState],
        }
    )

    return (
        <>
            <GridCell id="environments" title="Environments" loading={loading} padding={true}>
                <List
                    itemLayout="vertical"
                    size="default"
                    dataSource={data?.build?.slots}
                    renderItem={(slot) =>
                        <>
                            <List.Item
                                key={slot.id}
                            >
                                <Flex gap={16}>
                                    <Flex vertical={true} justify="flex-start">
                                        <EnvironmentIcon environmentId={slot.environment.id}/>
                                    </Flex>
                                    <Flex vertical={true} justify="flex-start" align="flex-start" gap={8} flex={1}>
                                        <BuildSlotInfo slot={slot} build={build}/>
                                    </Flex>
                                    <Flex vertical={true} justify="flex-start" gap={8} flex={3}>
                                        {
                                            isAuthorized(slot, "pipeline", "create") &&
                                            <SlotPipelineCreateButton
                                                slot={slot}
                                                build={build}
                                                onStart={refresh}
                                                text="Create candidate deployment"
                                            />
                                        }
                                        {
                                            slot.currentPipeline && !slot.currentPipeline.finished && slot.currentPipeline.build.id !== build.id &&
                                            <>
                                                <Typography.Text type="secondary">
                                                    Another build is being deployed
                                                </Typography.Text>
                                                <BuildDeploymentListItem
                                                    deployment={slot.currentPipeline}
                                                    build={slot.currentPipeline.build}
                                                    refresh={refresh}
                                                />
                                            </>
                                        }
                                        {
                                            slot.pipelines.pageItems.length === 0 &&
                                            <Typography.Text type="secondary">This build was not deployed
                                                yet</Typography.Text>
                                        }
                                        {
                                            slot.pipelines.pageItems.length > 0 &&
                                            <>
                                                <Typography.Text type="secondary">Deployments for this
                                                    build</Typography.Text>
                                                <List
                                                    size="small"
                                                    dataSource={slot.pipelines.pageItems}
                                                    renderItem={(deployment) =>
                                                        <BuildDeploymentListItem
                                                            deployment={deployment}
                                                            refresh={refresh}
                                                        />
                                                    }
                                                />
                                            </>
                                        }
                                    </Flex>
                                </Flex>
                            </List.Item>
                        </>
                    }
                />
            </GridCell>
        </>
    )
}