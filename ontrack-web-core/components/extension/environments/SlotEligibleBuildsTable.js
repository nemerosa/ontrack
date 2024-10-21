import {gql} from "graphql-request";
import {gqlSlotPipelineBuildData} from "@components/extension/environments/EnvironmentGraphQL";
import StandardTable from "@components/common/table/StandardTable";
import {Space} from "antd";
import BuildLink from "@components/builds/BuildLink";
import PromotionRuns from "@components/promotionRuns/PromotionRuns";
import {isAuthorized} from "@components/common/authorizations";
import SlotPipelineCreateButton from "@components/extension/environments/SlotPipelineCreateButton";

export default function SlotEligibleBuildsTable({slot, onChange}) {
    return (
        <>
            <StandardTable
                query={
                    gql`
                        query SlotEligibleBuilds($id: String!) {
                            slotById(id: $id) {
                                eligibleBuilds(size: 5) {
                                    pageInfo {
                                        nextPage {
                                            offset
                                            size  
                                        }                                    
                                    }
                                    pageItems {
                                        ...SlotPipelineBuildData
                                    }
                                }
                            }
                        }
                        ${gqlSlotPipelineBuildData}
                    `
                }
                variables={{id: slot.id}}
                queryNode={data => data.slotById.eligibleBuilds}
                filter={{}}
                columns={[
                    {
                        key: 'build',
                        title: 'Build',
                        render: (_, build) => <Space>
                            <BuildLink build={build}/>
                            <PromotionRuns promotionRuns={build.promotionRuns}/>
                            {
                                isAuthorized(slot, "pipeline", "create") &&
                                <SlotPipelineCreateButton
                                    slot={slot}
                                    build={build}
                                    onStart={onChange}
                                />
                            }
                        </Space>
                    }
                ]}
            />
        </>
    )
}