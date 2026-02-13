import {gql} from "graphql-request";
import {
    gqlAutoVersioningBranchTrailContent
} from "@components/extension/auto-versioning/AutoVersioningGraphQLFragments";
import AutoVersioningTrailTable from "@components/extension/auto-versioning/AutoVersioningTrailTable";

export default function AutoVersioningPromotionLevelTrail({promotionLevelId}) {
    return (
        <>
            <AutoVersioningTrailTable
                query={
                    gql`
                        query AutoVersioningPromotionLevelTrail(
                            $promotionLevelId: Int!,
                            $onlyEligible: Boolean = true,
                            $projectName: String = null,
                        ) {
                            promotionLevel(id: $promotionLevelId) {
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
                    promotionLevelId: Number(promotionLevelId),
                }}
                queryNode={data => data.promotionLevel.autoVersioningTrailPaginated}
            />
        </>
    )
}