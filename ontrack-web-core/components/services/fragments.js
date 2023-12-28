import {gql} from "graphql-request";

export const gqlPromotionLevelFragment = gql`
    fragment PromotionLevelData on PromotionLevel {
        id
        name
        description
        image
        authorizations {
            name
            action
            authorized
        }
        branch {
            id
            name
            project {
                id
                name
            }
        }
    }
`

export const gqlPromotionLevelByIdQuery = gql`
    query PromotionLevelById($id: Int!) {
        promotionLevel(id: $id) {
            ...PromotionLevelData
        }
    }
    ${gqlPromotionLevelFragment}
`

export const getPromotionLevelById = (client, id) => {
    return client.request(
        gqlPromotionLevelByIdQuery,
        {id}
    ).then(data => data.promotionLevel)
}

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

export const gqlInformationFragment = gql`
    fragment informationFragment on EntityInformation {
        type
        data
    }
`
