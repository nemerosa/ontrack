import {useMutation, useQuery} from "@components/services/GraphQL";
import {gql} from "graphql-request";

const gqlToken = gql`
    fragment TokenData on Token {
        name
        value
        creation
        lastUsed
        validUntil
        valid
    }
`

export const useTokens = ({refreshState}) => {
    const {data: tokens, loading} = useQuery(
        gql`
            query UserTokens {
                user {
                    account {
                        id
                        tokens {
                            ...TokenData
                        }
                    }
                }
            }
            ${gqlToken}
        `,
        {
            initialData: [],
            deps: [refreshState],
            dataFn: data => data.user?.account?.tokens ?? []
        }
    )
    return {tokens, loading}
}

export const useGenerateToken = ({refresh}) => {
    const {mutate, data, loading, error} = useMutation(
        gql`
            mutation GenerateToken($name: String!) {
                generateToken(input: {name: $name}) {
                    errors {
                        message
                    }
                    token {
                        ...TokenData
                    }
                }
            }
            ${gqlToken}
        `,
        {
            userNodeName: 'generateToken',
            onSuccess: refresh,
        }
    )

    const generateToken = async ({name}) => {
        await mutate({
            name
        })
    }

    return {
        generateToken,
        data,
        loading,
        error,
    }
}


export const useRevokeToken = ({refresh}) => {
    const {mutate, data, loading, error} = useMutation(
        gql`
            mutation RevokeToken($name: String!) {
                revokeToken(input: {name: $name}) {
                    errors {
                        message
                    }
                }
            }
        `,
        {
            userNodeName: 'revokeToken',
            onSuccess: refresh,
        }
    )

    const revokeToken = async ({name}) => {
        await mutate({
            name
        })
    }

    return {
        revokeToken,
    }
}
