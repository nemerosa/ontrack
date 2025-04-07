import {createContext} from "react";
import {signIn, useSession} from "next-auth/react";

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
                    status === "loading" &&
                    <div>Loading...</div>
                }
                {
                    status === "authenticated" && children
                }
            </AuthContext.Provider>
        </>
    )
}