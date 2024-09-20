import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gqlBranchContentFragment} from "@components/branches/BranchGraphQLFragments";

export const gqlUserMenuActionFragment = gql`
    fragment userMenuActionFragment on UserMenuAction {
        groupId
        extension
        id
        name
    }
`

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
        userMenuActions {
            ...userMenuActionFragment
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
    ${gqlUserMenuActionFragment}
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
        charts {
            id
            title
            type
            config
            parameters
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

export const getValidationStampById = (client, id) => {
    return client.request(
        gqlValidationStampByIdQuery,
        {id}
    ).then(data => data.validationStamp)
}

export const useValidationStamp = (id) => {
    const client = useGraphQLClient()
    const [validationStamp, setValidationStamp] = useState()
    useEffect(() => {
        if (client && id) {
            getValidationStampById(client, id).then(vs => setValidationStamp(vs))
        }
    }, [client, id]);
    return validationStamp
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

/**
 * @deprecated Use gqlProjectContentFragment
 */
export const gqlProjectCommonFragment = gql`
    fragment projectCommonFragment on Project {
        id
        name
    }
`

/**
 * @deprecated Use gqlBranchContentFragment
 */
export const gqlBranchCommonFragment = gql`
    fragment branchCommonFragment on Branch {
        ...BranchContent
    }

    ${gqlBranchContentFragment}
`

export const gqlValidationStampByIdQuery = gql`
    query ValidationStampById($id: Int!) {
        validationStamp(id: $id) {
            ...ValidationStampData
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
    ${gqlValidationStampFragment}
    ${gqlPropertiesFragment}
    ${gqlInformationFragment}
    ${gqlUserMenuActionFragment}
`

export const gqlPromotionLevelByIdQuery = gql`
    query PromotionLevelById($id: Int!) {
        promotionLevel(id: $id) {
            ...PromotionLevelData
            properties {
                ...propertiesFragment
            }
            information {
                ...informationFragment
            }
        }
    }
    ${gqlPromotionLevelFragment}
    ${gqlPropertiesFragment}
    ${gqlInformationFragment}
`
