import {createContext, useContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookieName} from "@/connectionConstants";
import {getCookie} from "cookies-next";

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

export const useGraphQL = (query, variables) => {
    const connection = useConnection()

    const [loading, setLoading] = useState(true)
    const [data, setData] = useState()

    connection.graphQLCall(query, variables)
        .then(data => {
            setData(data)
        })
        .finally(() => {
            setLoading(false)
        })

    return {
        loading,
        data,
    }
}

export const ConnectionContext = createContext({})

export default function ConnectionContextProvider({children}) {

    const router = useRouter()

    const [context, setContext] = useState({})

    useEffect(() => {
        console.log("[connection][provider] Route changed, checking cookie")
        const cookie = getCookie(cookieName)
        if (cookie) {
            console.log("[connection][provider] Cookie present, checking token")
            if (cookie && cookie !== context.token) {
                console.log("[connection][provider] Cookie changed, updating context")
                const config = createConnectionConfig(cookie)
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