import {createContext, useEffect, useState} from "react";
import restCall from "@client/restCall";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export const UserContext = createContext({user: {}});

const UserContextProvider = ({children}) => {
    const [user, setUser] = useState({});

    useEffect(() => {
        restCall("/rest/user").then(data => {
            setUser(data)
        });
    }, []);

    useEffect(() => {
        if (user) {
            graphQLCall(
                gql`
                    query UserAuthorizations {
                        authorizations {
                            name
                            action
                            authorized
                        }
                    }
                `
            ).then(data => {
                const authorizations = data.authorizations
                // Indexing
                user.authorizations = {}
                authorizations.forEach(authorization => {
                    let domain = user.authorizations[authorization.name]
                    if (!domain) {
                        domain = {}
                        user.authorizations[authorization.name] = domain
                    }
                    domain[authorization.action] = authorization.authorized
                })
                // Utility function
                user.isAuthorized = (domain, action) => (user.authorizations[domain]?.[action] === true)
            })
        }
    }, [user])

    return <UserContext.Provider value={user}>{children}</UserContext.Provider>
};

export default UserContextProvider;
