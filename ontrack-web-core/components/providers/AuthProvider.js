import {createContext} from "react";
import {useSession} from "next-auth/react";
import UserLoginPage from "@components/providers/UserLoginPage";

export const AuthContext = createContext({session: null})

export default function AuthProvider({children}) {

    const {data: session, status} = useSession()

    const context = {
        session,
    }

    return (
        <>
            <AuthContext.Provider value={context}>
                {
                    status === "loading" &&
                    <div>Loading...</div>
                }
                {
                    status === "unauthenticated" && <UserLoginPage/>
                }
                {
                    status === "authenticated" && children
                }
            </AuthContext.Provider>
        </>
    )
}