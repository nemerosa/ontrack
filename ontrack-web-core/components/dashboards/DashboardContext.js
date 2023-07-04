import {createContext, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export const DashboardContext = createContext({})

const DashboardContextProvider = ({context, contextId = "-", children}) => {

    const [dashboard, setDashboard] = useState({context, contextId})

    useEffect(() => {
        if (context && contextId) {
            graphQLCall(
                gql`
                    query Dashboard($context: String!, $contextId: String!) {
                        dashboardByContext(key: $context, id: $contextId) {
                            key
                            name
                            layoutKey
                            widgets {
                                key
                                config
                            }
                        }
                    }
                `,
                {context, contextId}
            ).then(data => {
                setDashboard(data.dashboardByContext)
            })
        }
    }, [context, contextId])

    return <DashboardContext.Provider value={dashboard}>{children}</DashboardContext.Provider>
}

export default DashboardContextProvider
