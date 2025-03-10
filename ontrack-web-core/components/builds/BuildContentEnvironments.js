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
import {slotNameWithoutProject} from "@components/extension/environments/SlotName";
import Link from "next/link";
import {slotUri} from "@components/extension/environments/EnvironmentsLinksUtils";
import {FaArrowUp} from "react-icons/fa";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";

export default function BuildContentEnvironments({build}) {

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
                        pipelines(buildId: $id) {
                            pageItems {
                                ...SlotPipelineDataNoBuild
                            }
                        }
                    }
                }
            }
        `,
        {
            variables: {id: build.id},
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
                                    <Flex vertical={true} justify="flex-start" gap={8}>
                                        <Link href={slotUri(slot)}>
                                            <Typography.Text strong>
                                                {slotNameWithoutProject(slot)}
                                            </Typography.Text>
                                        </Link>
                                        {
                                            slot.lastDeployedPipeline &&
                                            <Space direction="vertical">
                                                <Typography.Text type="secondary">
                                                    <Space>
                                                        <FaArrowUp/>
                                                        Deployed
                                                    </Space>
                                                </Typography.Text>
                                                <Space>
                                                    <BuildLink build={slot.lastDeployedPipeline.build}/>
                                                    <PromotionRuns
                                                        promotionRuns={slot.lastDeployedPipeline.build.promotionRuns}/>
                                                </Space>
                                            </Space>
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