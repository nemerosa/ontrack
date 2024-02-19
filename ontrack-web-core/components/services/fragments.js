import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";

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

export const usePromotionLevel = (id) => {
    const client = useGraphQLClient()
    const [promotionLevel, setPromotionLevel] = useState()
    useEffect(() => {
        if (client && id) {
            getPromotionLevelById(client, id).then(pl => setPromotionLevel(pl))
        }
    }, [client, id]);
    return promotionLevel
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

export const gqlUserMenuActionFragment = gql`
    fragment userMenuActionFragment on UserMenuAction {
        groupId
        extension
        id
        name
    }
`
