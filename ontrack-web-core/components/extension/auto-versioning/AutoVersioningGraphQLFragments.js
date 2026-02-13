import {gql} from "graphql-request";

export const gqlAutoVersioningBranchTrailContent = gql`
    fragment AutoVersioningBranchTrailContent on AutoVersioningBranchTrail {
        branch {
            id
            name
            displayName
            project {
                id
                name
            }
        }
        configuration {
            autoApproval
            autoApprovalMode
            targetPath
        }
        rejectionReason
        orderId
        audit {
            mostRecentState {
                state
                data
            }
        }
    }
`

export const gqlAutoVersioningTrailContent = gql`
    fragment AutoVersioningTrailContent on AutoVersioningTrail {
        branches {
            ...AutoVersioningBranchTrailContent
        }
    }
    ${gqlAutoVersioningBranchTrailContent}
`
