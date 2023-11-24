import {createContext, useEffect, useState} from "react";
import {useRouter} from "next/router";
import {cookieName} from "@/connectionConstants";
import {getCookie} from "cookies-next";

export const ConnectionContext = createContext({
    token: ''
})

export default function ConnectionContextProvider({children}) {

    const router = useRouter()

    const [context, setContext] = useState({
        token: ''
    })

    useEffect(async () => {
        console.log("[connection][provider] Route changed, checking cookie")
        const cookie = getCookie(cookieName)
        if (cookie) {
            if (cookie.value && cookie.value !== context.token) {
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