import {gql} from "graphql-request";

export const gqlValidationStampFilterFragment = gql`
    fragment validationStampFilterContent on ValidationStampFilter {
        id
        name
        scope
        vsNames
        authorizations {
            name
            action
            authorized
        }
    }
`