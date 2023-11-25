import {createContext, useContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookieName, isConnectionLoggingEnabled, ontrackUrl} from "@/connection";
import {getCookie} from "cookies-next";
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