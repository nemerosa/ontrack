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

export const gqlValidationStampFragment = gql`
    fragment ValidationStampData on ValidationStamp {
        id
        name
        description
        image
        dataType {
            descriptor {
                id
                displayName
            }
            config
        }
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

export const gqlValidationStampByIdQuery = gql`
    query ValidationStampById($id: Int!) {
        validationStamp(id: $id) {
            ...ValidationStampData
        }
    }
    ${gqlValidationStampFragment}
`

export const getValidationStampById = (client, id) => {
    return client.request(
        gqlValidationStampByIdQuery,
        {id}
    ).then(data => data.validationStamp)
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
        title
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

export const gqlProjectCommonFragment = gql`
    fragment projectCommonFragment on Project {
        id
        name
    }
`

export const gqlBranchCommonFragment = gql`
    fragment branchCommonFragment on Branch {
        id
        name
        project {
            ...projectCommonFragment
        }
    }
    
    ${gqlProjectCommonFragment}
`
