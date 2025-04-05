import {createContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useQuery} from "@components/services/GraphQL";
import LoadingContainer from "@components/common/LoadingContainer";

export const UserContext = createContext({
    name: '',
    authorizations: {},
    userMenuGroups: []
})

export default function UserContextProvider({children}) {

    const [user, setUser] = useState({
        name: '',
        authorizations: {},
        userMenuGroups: [],
    })

    const {data, loading, error, finished} = useQuery(
        gql`
            query UserContext {
                user {
                    account {
                        name
                    }
                }
                userMenuItems {
                    id
                    name
                    items {
                        extension
                        id
                        name
                    }
                }
                authorizations {
                    name
                    action
                    authorized
                }
            }
        `
    )

    useEffect(() => {
        if (data && finished) {
            const tmpUser = {
                name: data?.user?.account?.name
            }
            console.log({data, tmpUser})
            // Groups
            tmpUser.userMenuGroups = data.userMenuItems
            // Indexing of authorizations
            const authorizations = data.authorizations
            tmpUser.authorizations = {}
            authorizations.forEach(authorization => {
                let domain = tmpUser.authorizations[authorization.name]
                if (!domain) {
                    domain = {}
                    tmpUser.authorizations[authorization.name] = domain
                }
                domain[authorization.action] = authorization.authorized
            })
            // We're done
            setUser(tmpUser)
        }
    }, [data, finished])

    return (
        <>
            <LoadingContainer loading={loading} error={error}>
                <UserContext.Provider value={user}>{children}</UserContext.Provider>
            </LoadingContainer>
        </>
    )

}
