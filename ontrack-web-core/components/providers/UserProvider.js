import {createContext, useEffect, useState} from "react";
import {gql} from "graphql-request";
import {useGraphQLClient, useRestClient} from "@components/providers/ConnectionContextProvider";

export const UserContext = createContext({user: {}});

const UserContextProvider = ({children}) => {

    const gqlClient = useGraphQLClient()
    const restClient = useRestClient()

    const [user, setUser] = useState({authorizations: {}});

    let tmpUser = {}

    useEffect(() => {
        if (restClient && gqlClient) {
            restClient.get("/rest/user").then(data => {
                tmpUser = data
                return gqlClient.request(
                    gql`
                        query UserAuthorizations {
                            authorizations {
                                name
                                action
                                authorized
                            }
                        }
                    `
                )
            }).then(data => {
                const authorizations = data.authorizations
                // Indexing
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
            })
        }
    }, [gqlClient, restClient])

    return <UserContext.Provider value={user}>{children}</UserContext.Provider>
};

export default UserContextProvider;
