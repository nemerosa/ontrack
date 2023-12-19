import {createContext, useContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookieName, cookieOptions} from "@/connection";
import {deleteCookie, getCookie} from "cookies-next";
import {GraphQLClient} from "graphql-request";

function createConnectionConfig(environment, token) {
    const connectionConfig = {
        url: environment.ontrack.url,
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
                    console.log("logout", {connection, config})
                    location.href = `${config.url}/login?logout&targetUrl=${connection.environment.ontrack.ui.url}`
                },
            })
        }
    }, [connection.config]);
    return logout
}

export const ConnectionContext = createContext({})

export default function ConnectionContextProvider({environment, children}) {

    const router = useRouter()
    const logging = environment.ontrack.connection.logging
    const tracing = environment.ontrack.connection.tracing

    const [context, setContext] = useState({})

    useEffect(() => {
        if (tracing) console.log("[connection][provider] Route changed, checking cookie")
        const cookie = getCookie(cookieName)
        if (cookie) {
            if (tracing) console.log("[connection][provider] Cookie present, checking token")
            if (cookie && cookie !== context.token) {
                if (tracing) console.log("[connection][provider] Cookie changed, updating context")
                const config = createConnectionConfig(environment, cookie)
                if (tracing) console.log("[connection][provider] Using config ", config)
                if (tracing) console.log("[connection][provider] Using environment ", environment)
                setContext({
                    environment,
                    token: cookie,
                    config,
                })
            }
        } else {
            if (logging) console.log("[connection][provider] No cookie set. No connection is possible.")
        }
    }, [environment, router.asPath])

    return (
        <>
            <ConnectionContext.Provider value={context}>
                {children}
            </ConnectionContext.Provider>
        </>
    )
}