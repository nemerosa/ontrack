import {createContext, useContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookieName, cookieOptions, isConnectionLoggingEnabled, ontrackUiUrl, ontrackUrl} from "@/connection";
import {deleteCookie, getCookie} from "cookies-next";
import {GraphQLClient} from "graphql-request";

function createConnectionConfig(token) {
    const connectionConfig = {
        url: ontrackUrl()
    }
    if (process.env.NEXT_PUBLIC_LOCAL === 'true') {
        const username = "admin"
        const password = "admin"

        const token = btoa(`${username}:${password}`)

        connectionConfig.headers = {
            Authorization: `Basic ${token}`,
        }
    } else {
        connectionConfig.headers = {
            'X-Ontrack-Token': token,
        }
    }

    return connectionConfig
}

export const useConnection = () => useContext(ConnectionContext)

export const useGraphQLClient = () => {
    const connection = useConnection()
    const [client, setClient] = useState()
    useEffect(() => {
        if (connection.config) {
            const config = connection.config
            setClient(
                new GraphQLClient(
                    `${config.url}/graphql`,
                    {
                        headers: config.headers,
                    }
                )
            )
        }
    }, [connection.config])
    return client
}

export const useRestClient = () => {
    const connection = useConnection()
    const [client, setClient] = useState()
    useEffect(() => {
        if (connection.config) {
            const config = connection.config
            setClient({
                get: async (uri) => {
                    const response = await fetch(
                        `${config.url}${uri}`,
                        {
                            headers: config.headers,
                        }
                    )
                    return response.json()
                },
            })
        }
    }, [connection.config]);
    return client
}

export const useLogout = () => {
    const connection = useConnection()
    const [logout, setLogout] = useState()
    useEffect(() => {
        if (connection.config) {
            const config = connection.config
            setLogout({
                call: async () => {
                    // Removing the cookie
                    deleteCookie(cookieName, cookieOptions())
                    // Redirecting to the login page
                    location.href = `${config.url}/login?logout&targetUrl=${ontrackUiUrl()}`
                },
            })
        }
    }, [connection.config]);
    return logout
}

export const ConnectionContext = createContext({})

export default function ConnectionContextProvider({children}) {

    const router = useRouter()
    const logging = isConnectionLoggingEnabled()

    const [context, setContext] = useState({})

    useEffect(() => {
        if (logging) console.log("[connection][provider] Route changed, checking cookie")
        const cookie = getCookie(cookieName)
        if (cookie) {
            if (logging) console.log("[connection][provider] Cookie present, checking token")
            if (cookie && cookie !== context.token) {
                if (logging) console.log("[connection][provider] Cookie changed, updating context")
                const config = createConnectionConfig(cookie)
                if (logging) console.log("[connection][provider] Using config ", config)
                setContext({
                    token: cookie,
                    config,
                })
            }
        } else {
            if (logging) console.log("[connection][provider] No cookie set. No connection is possible.")
        }
    }, [router.asPath])

    return (
        <>
            <ConnectionContext.Provider value={context}>
                {children}
            </ConnectionContext.Provider>
        </>
    )
}