import {createContext} from "react";
import {signIn, useSession} from "next-auth/react";
import LoadingLogo from "@components/providers/LoadingLogo";

export const AuthContext = createContext({session: null})

export default function AuthProvider({children}) {

    const {data: session, status} = useSession()

    const context = {
        session,
    }

    if (status === "unauthenticated") {
        signIn()
        return null
    }

    return (
        <>
            <AuthContext.Provider value={context}>
                {
                    status !== "authenticated" ?
                        <LoadingLogo/> : children
                }
            </AuthContext.Provider>
        </>
    )
}