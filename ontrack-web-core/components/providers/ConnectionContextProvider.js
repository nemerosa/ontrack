import {createContext, useContext, useEffect, useMemo, useState} from "react";
import {callGraphQL} from "@components/services/GraphQL";

/**
 * @deprecated Remove for V5
 */
export const useConnection = () => useContext(ConnectionContext)

/**
 * @deprecated Remove for V5
 */
export const useFullRestUri = (uri) => {

    const connection = useConnection()
    const [fullUri, setFullUri] = useState('')

    useEffect(() => {
        if (connection.config) {
            setFullUri(`${connection.config.url}${uri}`)
        }
    }, [uri, connection])

    return {
        fullUri,
    }
}

/**
 * @deprecated Use useQuery / useMutation from GraphQL.js
 */
export const useGraphQLClient = () => {
    return useMemo(() => ({
        request: async (query, variables = {}) => {
            return await callGraphQL({query, variables});
        }
    }), [])
}

/**
 * Performs a REST call to the backend.
 * @deprecated Remove for V5
 */
export const useRestClient = () => {
    const connection = useConnection()
    const [client, setClient] = useState()
    useEffect(() => {
        if (connection.config) {
            const config = connection.config
            setClient({
                fetch: async (uri) => {
                    return fetch(
                        `${config.url}${uri}`,
                        {
                            headers: config.headers,
                        }
                    )
                },
                get: async (uri) => {
                    const response = await fetch(
                        `${config.url}${uri}`,
                        {
                            headers: config.headers,
                        }
                    )
                    return response.json()
                },
                put: async (uri, body) => {
                    await fetch(
                        `${config.url}${uri}`,
                        {
                            method: 'PUT',
                            headers: {
                                ...config.headers,
                                "Content-Type": "application/json",
                            },
                            body: body,
                        }
                    )
                }
            })
        }
    }, [connection.config]);
    return client
}

/**
 * @deprecated Remove for V5
 */
export const ConnectionContext = createContext({})
