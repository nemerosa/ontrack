import {gql} from "graphql-request";

/**
 * Minimal content for a branch
 */
export const gqlBranchContentFragment = gql`
    fragment BranchContent on Branch {
        id
        name
        displayName
        disabled
        project {
            id
            name
        }
    }
`
