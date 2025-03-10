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
import {FaArrowUp, FaPlay, FaStop} from "react-icons/fa";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import SlotPipelineLink from "@components/extension/environments/SlotPipelineLink";
import SlotPipelineStatusIcon from "@components/extension/environments/SlotPipelineStatusIcon";
import TimestampText from "@components/common/TimestampText";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";

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
                                    <Flex vertical={true} justify="flex-start" gap={8} flex={3}>
                                        {
                                            isAuthorized(slot, "pipeline", "create") &&
                                            <SlotPipelineCreateButton
                                                slot={slot}
                                                build={build}
                                                // TODO onStart={onChange}
                                                text="Create candidate deployment"
                                            />
                                        }
                                        {
                                            slot.pipelines.pageItems.length === 0 &&
                                            <Typography.Text type="secondary">This build was not deployed yet</Typography.Text>
                                        }
                                        {
                                            slot.pipelines.pageItems.length > 0 &&
                                            <>
                                                <Typography.Text type="secondary">Deployments for this build</Typography.Text>
                                                <List
                                                    size="small"
                                                    dataSource={slot.pipelines.pageItems}
                                                    renderItem={(deployment) =>
                                                        <List.Item
                                                            key={deployment.id}
                                                        >
                                                            <List.Item.Meta
                                                                avatar={
                                                                    <SlotPipelineStatusIcon
                                                                        status={deployment.status}
                                                                    />
                                                                }
                                                                title={
                                                                    <Space>
                                                                        Pipeline
                                                                        <SlotPipelineLink
                                                                            pipelineId={deployment.id}
                                                                            numberOnly={true}
                                                                        />
                                                                    </Space>
                                                                }
                                                                description={
                                                                    <Space direction="vertical">
                                                                        {
                                                                            deployment.end &&
                                                                            <Space>
                                                                                <FaStop/>
                                                                                <TimestampText value={deployment.end}/>
                                                                            </Space>
                                                                        }
                                                                        {
                                                                            !deployment.end &&
                                                                            <Space>
                                                                                <FaPlay/>
                                                                                <TimestampText
                                                                                    value={deployment.start}/>
                                                                            </Space>
                                                                        }
                                                                    </Space>
                                                                }
                                                            />
                                                        </List.Item>
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