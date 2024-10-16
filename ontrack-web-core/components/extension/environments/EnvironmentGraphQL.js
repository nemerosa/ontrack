import {gql} from "graphql-request";
import {gqlProjectContentFragment} from "@components/projects/ProjectGraphQLFragments";

export const gqlEnvironmentData = gql`
    fragment EnvironmentData on Environment {
        id
        name
        description
        order
        tags
    }
`;

export const gqlSlotData = gql`
    fragment SlotData on Slot {
        id
        project {
            ...ProjectContent
        }
        qualifier
        description
    }

    ${gqlProjectContentFragment}
`;
