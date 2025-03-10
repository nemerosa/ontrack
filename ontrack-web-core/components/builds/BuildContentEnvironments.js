import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {
    gqlSlotData,
    gqlSlotPipelineBuildData,
    gqlSlotPipelineDataNoBuild
} from "@components/extension/environments/EnvironmentGraphQL";
import {Flex, List, Space, Typography} from "antd";
import EnvironmentIcon from "@components/extension/environments/EnvironmentIcon";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";
import {useRefresh} from "@components/common/RefreshUtils";
import BuildDeploymentListItem from "@components/builds/environments/BuildDeploymentListItem";
import BuildSlotInfo from "@components/builds/environments/BuildSlotInfo";
import SelectEnvironmentName from "@components/extension/environments/SelectEnvironmentName";
import {useState} from "react";
import {AutoRefreshButton, AutoRefreshContextProvider} from "@components/common/AutoRefresh";

export default function BuildContentEnvironments({build}) {

    const [refreshState, refresh] = useRefresh()

    const [environmentName, setEnvironmentName] = useState()

    const {data, loading} = useQuery(
        gql`
            ${gqlSlotPipelineDataNoBuild}
            ${gqlSlotData}
            ${gqlSlotPipelineBuildData}
            query BuildEnvironments(
                $id: Int!,
                $environment: String,
            ) {
                build(id: $id) {
                    branch {
                        project {
                            name
                        }
                    }
                    slots(environment: $environment) {
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
            variables: {
                id: build.id,
                environment: environmentName,
            },
            deps: [refreshState, environmentName],
        }
    )

    return (
        <>
            <AutoRefreshContextProvider onRefresh={refresh}>
                <GridCell id="environments"
                          title="Environments"
                          loading={loading}
                          padding={true}
                          extra={
                              <Space>
                                  {
                                      data?.build?.branch?.project?.name &&
                                      <SelectEnvironmentName
                                          projects={[data.build.branch.project.name]}
                                          value={environmentName}
                                          onChange={setEnvironmentName}
                                      />
                                  }
                                  <AutoRefreshButton/>
                              </Space>
                          }
                >
                    <List
                        itemLayout="vertical"
                        size="small"
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
            </AutoRefreshContextProvider>
        </>
    )
}