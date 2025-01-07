import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingContainer from "@components/common/LoadingContainer";
import {gql} from "graphql-request";
import {Descriptions, Space} from "antd";
import YesNo from "@components/common/YesNo";
import SlotPipelineDeploymentStatusChecks from "@components/extension/environments/SlotPipelineDeploymentStatusChecks";
import SlotPipelineDeploymentChangeTable from "@components/extension/environments/SlotPipelineDeploymentChangeTable";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";
import TimestampText from "@components/common/TimestampText";
import {useReloadState} from "@components/common/StateUtils";
import PageSection from "@components/common/PageSection";
import SlotPipelineWorkflowsTable from "@components/extension/environments/SlotPipelineWorkflowsTable";

/**
 * @deprecated To delete
 */
export default function SlotPipelineDeploymentStatus({pipeline, onChange}) {

    const client = useGraphQLClient()

    const [reloadState, reload] = useReloadState({callback: onChange})

    const [loading, setLoading] = useState(true)
    const [items, setItems] = useState([])

    useEffect(() => {
        if (client && pipeline) {
            setLoading(true)
            client.request(
                gql`
                    query PipelineInfo($id: String!) {
                        slotPipelineById(id: $id) {
                            id
                            build {
                                ...SlotPipelineBuildData
                            }
                            status
                            start
                            end
                            deploymentStatus {
                                status
                                override
                            }
                            changes {
                                id
                                message
                                status
                                user
                                timestamp
                                overridden
                                overrideMessage
                            }
                        }
                    }
                    ${gqlSlotPipelineBuildData}
                `,
                {id: pipeline.id}
            ).then(data => {
                const slotPipeline = data.slotPipelineById
                const items = []

                const span = pipeline.end ? 3 : 4

                items.push({
                    key: 'status',
                    label: 'Status',
                    children: <SlotPipelineStatusActions
                        pipeline={pipeline}
                        info={true}
                        linkInfo={false}
                        actions={true}
                        onChange={reload}
                    />,
                    span: 4,
                })

                items.push({
                    key: 'build',
                    label: "Build",
                    span: 8,
                    children: <Space>
                        <BuildLink build={slotPipeline.build}/>
                        <PromotionRuns promotionRuns={slotPipeline.build.promotionRuns}/>
                    </Space>,
                })

                items.push({
                    key: 'start',
                    label: 'Started at',
                    children: <TimestampText value={pipeline.start}/>,
                    span: span,
                })

                if (pipeline.end) {
                    items.push({
                        key: 'end',
                        label: 'Ended at',
                        children: <TimestampText value={pipeline.end}/>,
                        span: span,
                    })
                }

                items.push({
                    key: 'deployable',
                    label: "Deployable",
                    span: span,
                    children: <YesNo value={slotPipeline.deploymentStatus.status}/>,
                })

                items.push({
                    key: 'override',
                    label: "Overridden",
                    span: span,
                    children: <YesNo value={slotPipeline.deploymentStatus.override}/>,
                })

                items.push({
                    key: 'checks',
                    span: 12,
                    children: <PageSection
                        title="Deployment checks"
                    >
                        <SlotPipelineDeploymentStatusChecks
                            pipeline={slotPipeline}
                            onChange={reload}
                        />
                    </PageSection>,
                })

                items.push({
                    key: 'workflows',
                    span: 12,
                    children: <PageSection
                        id={`pipeline-workflows-${pipeline.id}`}
                        title="Workflows"
                    >
                        <SlotPipelineWorkflowsTable
                            pipeline={slotPipeline}
                        />
                    </PageSection>
                })

                items.push({
                    key: 'changes',
                    span: 12,
                    children: <PageSection
                        title="Deployment history"
                    >
                        <SlotPipelineDeploymentChangeTable changes={slotPipeline.changes}/>
                    </PageSection>,
                })

                setItems(items)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pipeline, reloadState])

    return (
        <>
            <LoadingContainer loading={loading}>
                <Descriptions
                    loading={loading}
                    items={items}
                    column={12}
                    layout="horizontal"
                />
            </LoadingContainer>
        </>
    )
}