import {gql} from "graphql-request";
import {gqlInformationFragment, gqlPropertiesFragment, gqlUserMenuActionFragment} from "@components/services/fragments";
import {gqlBranchContentFragment} from "@components/branches/BranchGraphQLFragments";

export const gqlGetBranch = gql`
    query GetBranch($id: Int!) {
        branches(id: $id) {
            ...BranchContent
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
            userMenuActions {
                ...userMenuActionFragment
            }
        }
    }
    ${gqlBranchContentFragment}
    ${gqlPropertiesFragment}
    ${gqlInformationFragment}
    ${gqlUserMenuActionFragment}
`
