import {createContext} from "react";
import {useUser} from '@auth0/nextjs-auth0/client';
import UserLoginPage from "@components/providers/UserLoginPage";

export const AuthContext = createContext({user: null})

export default function AuthProvider({children}) {

    const {user, error, isLoading} = useUser()

    const context = {
        user,
    }

    return (
        <>
            <AuthContext.Provider value={context}>
                {
                    isLoading &&
                    <div>Loading...</div>
                }
                {
                    !isLoading && error &&
                    <div>{error.message}</div>
                }
                {
                    !isLoading && !user && <UserLoginPage/>
                }
                {
                    !isLoading && user && children
                }
            </AuthContext.Provider>
        </>
    )
}