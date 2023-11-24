import {createContext, useContext, useEffect, useRef, useState} from "react";
import {useRouter} from "next/router";
import {cookieName} from "@/connectionConstants";
import {getCookie} from "cookies-next";
import {GraphQLClient} from "graphql-request";

function createConnectionConfig(token) {
    const connectionConfig = {}
    if (process.env.NEXT_PUBLIC_LOCAL === 'true') {
        const url = "http://localhost:8080"
        const username = "admin"
        const password = "admin"

        const token = btoa(`${username}:${password}`)

        connectionConfig.url = url
        connectionConfig.headers = {
            Authorization: `Basic ${token}`,
        }
    } else {
        connectionConfig.url = process.env.ONTRACK_URL ?? 'http://localhost:8080'
        connectionConfig.headers = {
            'X-Ontrack-Token': token,
        }
    }
    return connectionConfig
}

export const useConnection = () => useContext(ConnectionContext)

export const useGraphQL = (query, variables, callback, onCompleted) => {
    const connection = useConnection()
    connection.graphQLCall(query, variables)
        .then(data => {
            if (callback) {
                return callback(data);
            }
        })
        .finally(() => {
            if (onCompleted) {
                onCompleted()
            }
        })
}

export const ConnectionContext = createContext({
    token: '',
    config: {
        url: '',
        headers: {},
    },
    client: null,
    graphQLCall: () => {
    },
    restCall: () => {
    },
    logout: () => {
    },
})

export default function ConnectionContextProvider({children}) {

    const router = useRouter()

    const context = useRef({
        token: '',
        config: {
            url: '',
            headers: {},
        },
        client: undefined,
    })

    useEffect(() => {
        console.log("[connection][provider] Route changed, checking cookie")
        const cookie = getCookie(cookieName)
        if (cookie) {
            if (cookie.value && cookie.value !== context.token) {
                const token = cookie.value
                console.log("[connection][provider] Cookie changed, updating context")
                const config = createConnectionConfig(token)
                const client = new GraphQLClient(
                    `${config.url}/graphql`, {
                        headers: config.headers,
                    })
                context.current = {
                    token,
                    config,
                    client,
                    graphQLCall: async (query, variables) => client.request(query, variables),
                    restCall: async (uri) => {
                        const response = await fetch(
                            `${config.url}${uri}`,
                            {
                                headers: config.headers,
                            }
                        )
                        return response.json()
                    },
                    logout: async () => {
                        await fetch(`${config.url}/logout`, {method: 'POST'})
                        location.href = `${config.url}/login?logout`
                    },
                }
            }
        }
    }, [router.asPath])

    return (
        <>
            <ConnectionContext.Provider value={context.current}>
                {children}
            </ConnectionContext.Provider>
        </>
    )
}