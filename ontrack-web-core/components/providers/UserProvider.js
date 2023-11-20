import {createContext, useEffect, useState} from "react";
import restCall from "@client/restCall";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export const UserContext = createContext({user: {}});

const UserContextProvider = ({children}) => {
    const [user, setUser] = useState({authorizations:{}});

    let tmpUser = {}

    useEffect(() => {
        restCall("/rest/user").then(data => {
            tmpUser = data
            return graphQLCall(
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
    }, []);

    return <UserContext.Provider value={user}>{children}</UserContext.Provider>
};

export default UserContextProvider;
