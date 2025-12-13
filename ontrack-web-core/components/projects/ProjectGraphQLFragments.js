import {gql} from "graphql-request";

/**
 * Minimal content for a project
 */
export const gqlProjectContentFragment = gql`
    fragment ProjectContent on Project {
        id
        name
        disabled
        description
        annotatedDescription
    }
`
