import {createContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookies} from "next/headers";
import {cookieName} from "@/connectionConstants";

export const ConnectionContext = createContext({
    token: ''
})

export default function ConnectionContextProvider({children}) {

    const router = useRouter()

    const [context, setContext] = useState({
        token: ''
    })

    useEffect(() => {
        console.log("[connection][provider] Route changed, checking cookie")
        const cookie = cookies().get(cookieName)
        if (cookie) {
            if (cookie.value !== context.token) {
                console.log("[connection][provider] Cookie changed, updating context")
                setContext({
                    token: cookie.value,
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