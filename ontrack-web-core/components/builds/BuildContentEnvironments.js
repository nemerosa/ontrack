import GridCell from "@components/grid/GridCell";
import {useQuery} from "@components/services/useQuery";
import {gql} from "graphql-request";
import {
    gqlSlotData,
    gqlSlotPipelineBuildData,
    gqlSlotPipelineDataNoBuild
} from "@components/extension/environments/EnvironmentGraphQL";
import {List, Space} from "antd";
import {useRefresh} from "@components/common/RefreshUtils";
import SelectEnvironmentName from "@components/extension/environments/SelectEnvironmentName";
import {useState} from "react";
import {AutoRefreshButton, AutoRefreshContextProvider} from "@components/common/AutoRefresh";
import BuildEnvironment from "@components/builds/environments/BuildEnvironment";

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
                                    <BuildEnvironment slot={slot} build={build} refresh={refresh}/>
                                </List.Item>
                            </>
                        }
                    />
                </GridCell>
            </AutoRefreshContextProvider>
        </>
    )
}