import {gql} from "graphql-request";

export const gqlAutoVersioningTrailContent = gql`
    fragment AutoVersioningTrailContent on AutoVersioningTrail {
        branches {
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
    }
`
