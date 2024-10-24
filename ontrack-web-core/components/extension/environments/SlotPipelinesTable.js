import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import {gqlSlotPipelineData} from "@components/extension/environments/EnvironmentGraphQL";
import {Space} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import TimestampText from "@components/common/TimestampText";
import SlotPipelineStatusActions from "@components/extension/environments/SlotPipelineStatusActions";
import {useReloadState} from "@components/common/StateUtils";
import Link from "next/link";
import {slotPipelineUri} from "@components/extension/environments/EnvironmentsLinksUtils";

export default function SlotPipelinesTable({slot}) {

    const [reloadState, reload] = useReloadState()

    return (
        <>
            <StandardTable
                query={
                    gql`
                        query SlotPipelines($id: String!) {
                            slotById(id: $id) {
                                pipelines(offset: 0, size: 10) {
                                    pageInfo {
                                        nextPage {
                                            offset
                                            size                                    
                                        }                                    
                                    }
                                    pageItems {
                                        ...SlotPipelineData
                                    }
                                }
                            }
                        }
                        ${gqlSlotPipelineData}
                    `
                }
                queryNode={data => data.slotById.pipelines}
                reloadCount={reloadState}
                filter={{}}
                variables={{
                    id: slot.id,
                }}
                columns={[
                    {
                        key: 'number',
                        title: 'Number',
                        render: (_, item) => <Link href={slotPipelineUri(item.id)}>{`#${item.number}`}</Link>,
                    },
                    {
                        key: 'build',
                        title: 'Build',
                        render: (_, item) => <Space>
                            <BuildLink build={item.build}/>
                            <PromotionRuns promotionRuns={item.build.promotionRuns}/>
                        </Space>
                    },
                    {
                        key: 'start',
                        title: 'Start time',
                        render: (_, item) => <TimestampText value={item.start}/>,
                    },
                    {
                        key: 'end',
                        title: 'End time',
                        render: (_, item) => <TimestampText value={item.end}/>,
                    },
                    {
                        key: 'actions',
                        title: 'Actions',
                        render: (_, item) => <SlotPipelineStatusActions
                            pipeline={item}
                            onChange={reload}
                        />,
                    }
                ]}
            />
        </>
    )
}