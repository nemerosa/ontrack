import {gql} from "graphql-request";

export const gqlDecorationFragment = gql`
    fragment decorationContent on Decoration {
        decorationType
        error
        data
        feature {
            id
        }
    }
`

export const gqlPropertiesFragment = gql`
    fragment propertiesFragment on Property {
        type {
            typeName
            name
        }
        editable
        value
    }
`
