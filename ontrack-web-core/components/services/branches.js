import {gql} from "graphql-request";
import {gqlInformationFragment, gqlPropertiesFragment} from "@components/services/fragments";

export const gqlGetBranch = gql`
    query GetBranch($id: Int!) {
        branches(id: $id) {
            id
            name
            project {
                id
                name
            }
            authorizations {
                name
                action
                authorized
            }
            properties {
                ...propertiesFragment
            }
            information {
                ...informationFragment
            }
        }
    }
    ${gqlPropertiesFragment}
    ${gqlInformationFragment}
`
