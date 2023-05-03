import {createContext, useEffect, useState} from "react";
import restCall from "@client/restCall";

export const UserContext = createContext({user: {}});

const UserContextProvider = ({children}) => {
    const [user, setUser] = useState({});
    useEffect(() => {
        restCall("/rest/user").then(data => {
            setUser(data)
        });
    }, []);

    return <UserContext.Provider value={user}>{children}</UserContext.Provider>
};

export default UserContextProvider;
