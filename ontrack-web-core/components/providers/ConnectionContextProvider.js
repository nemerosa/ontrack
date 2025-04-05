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

/**
 * @deprecated Remove for V5
 */
export default function ConnectionContextProvider({environment, children}) {

    // const router = useRouter()
    // const logging = environment.ontrack.connection.logging
    // const tracing = environment.ontrack.connection.tracing

    const [context, setContext] = useState({})

    // useEffect(() => {
    //     if (tracing) console.log("[connection][provider] Route changed, checking cookie")
    //     const cookie = getCookie(cookieName)
    //     if (cookie) {
    //         if (tracing) console.log("[connection][provider] Cookie present, checking token")
    //         if (cookie && cookie !== context.token) {
    //             if (tracing) console.log("[connection][provider] Cookie changed, updating context")
    //             const config = createConnectionConfig(environment, cookie)
    //             if (tracing) console.log("[connection][provider] Using config ", config)
    //             if (tracing) console.log("[connection][provider] Using environment ", environment)
    //             setContext({
    //                 environment,
    //                 token: cookie,
    //                 config,
    //             })
    //         }
    //     } else {
    //         if (logging) console.log("[connection][provider] No cookie set. No connection is possible.")
    //     }
    // }, [environment, router.asPath])

    return (
        <>
            <ConnectionContext.Provider value={context}>
                {children}
            </ConnectionContext.Provider>
        </>
    )
}