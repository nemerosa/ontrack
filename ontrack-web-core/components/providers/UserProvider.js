import {createContext} from "react";
import {gql} from "graphql-request";
import {useQuery} from "@components/services/GraphQL";
import LoadingContainer from "@components/common/LoadingContainer";

export const UserContext = createContext({
    name: '',
    authorizations: {},
    userMenuGroups: []
})

export default function UserContextProvider({children}) {
    const {data: user, loading, error} = useQuery(
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
        `,
        {
            initialData: {
                name: '',
                authorizations: {},
                userMenuGroups: [],
            },
            dataFn: data => {
                const tmpUser = {
                    name: data.user.account.name
                }
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
                return tmpUser
            }
        }
    )

    return (
        <>
            <LoadingContainer loading={loading} error={error}>
                <UserContext.Provider value={user}>{children}</UserContext.Provider>
            </LoadingContainer>
        </>
    )

}
