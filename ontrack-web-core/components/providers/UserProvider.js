import {createContext, useEffect, useState} from "react";
import restCall from "@client/restCall";
import {gql} from "graphql-request";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export const UserContext = createContext({user: {}});

const UserContextProvider = ({children}) => {

    const client = useGraphQLClient()

    const [user, setUser] = useState({authorizations:{}});

    let tmpUser = {}

    useEffect(() => {
        if (client) {
            restCall("/rest/user").then(data => {
                tmpUser = data
                return client.request(
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
    }, [client])

    return <UserContext.Provider value={user}>{children}</UserContext.Provider>
};

export default UserContextProvider;
