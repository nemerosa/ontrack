import {gql} from "graphql-request";
import {
    gqlAutoVersioningBranchTrailContent
} from "@components/extension/auto-versioning/AutoVersioningGraphQLFragments";
import PageSection from "@components/common/PageSection";
import {Typography} from "antd";
import AutoVersioningTrailTable, {
    autoVersioningTrailAuditColumn
} from "@components/extension/auto-versioning/AutoVersioningTrailTable";

export default function AutoVersioningPromotionRunTrail({promotionRunId}) {
    return (
        <>
            <PageSection
                id="auto-versioning-trail"
                title="Auto-versioning trail"
                padding={false}
            >
                <Typography.Paragraph type="secondary" style={{padding: 8}}>
                    List of auto-versioning targets for this promotion run.
                </Typography.Paragraph>
                <AutoVersioningTrailTable
                    query={
                        gql`
                            query AutoVersioningPromotionRunTrail(
                                $promotionRunId: Int!,
                                $onlyEligible: Boolean = true,
                                $projectName: String = null,
                            ) {
                                promotionRuns(id: $promotionRunId) {
                                    autoVersioningTrailPaginated(
                                        filter: {
                                            onlyEligible: $onlyEligible,
                                            projectName: $projectName,
                                        }
                                    ) {
                                        pageInfo {
                                            nextPage {
                                                offset
                                                size
                                            }
                                        }
                                        pageItems {
                                            ...AutoVersioningBranchTrailContent
                                        }
                                    }
                                }
                            }
                            ${gqlAutoVersioningBranchTrailContent}
                        `
                    }
                    variables={{
                        promotionRunId: Number(promotionRunId),
                    }}
                    queryNode={data => data.promotionRuns[0].autoVersioningTrailPaginated}
                    extraColumns={[
                        autoVersioningTrailAuditColumn
                    ]}
                />
            </PageSection>
        </>
    )
}