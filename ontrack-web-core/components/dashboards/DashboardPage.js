import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import {useEffect, useReducer} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export const dashboardReducer = (dashboard, action) => {
    switch (action.type) {
        case 'init': {
            return action.data
        }
        case 'startEdition': {
            return {
                ...dashboard,
                editionMode: true,
            }
        }
        case 'cancelEdition': {
            return {
                ...dashboard,
                editionMode: false,
            }
        }
        default: {
            throw Error('Unknown action: ' + action.type);
        }
    }
}
export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    const [dashboard, dashboardDispatch] = useReducer(dashboardReducer, {})

    useEffect(() => {
        if (context && contextId) {
            graphQLCall(
                gql`
                    query Dashboard($context: String!, $contextId: String!) {
                        dashboardByContext(key: $context, id: $contextId) {
                            key
                            name
                            builtIn
                            layoutKey
                            widgets {
                                uuid
                                key
                                config
                            }
                        }
                    }
                `,
                {context, contextId}
            ).then(data => {
                // Setting the initial state of the dashboard
                dashboardDispatch({
                    type: 'init',
                    data: {
                        context,
                        contextId,
                        editionMode: false,
                        ...data.dashboardByContext,
                    }
                })
            })
        }
    }, [context, contextId])

    const commands = [
        <DashboardCommandMenu key="dashboard-commands"/>,
        <CloseCommand key="close" href={closeHref}/>,
    ]

    return (
        <>
            <DashboardContext.Provider value={dashboard}>
                <DashboardDispatchContext.Provider value={dashboardDispatch}>
                    <MainPage
                        title={title}
                        breadcrumbs={breadcrumbs}
                        commands={commands}
                    >
                        <LoadingContainer loading={loading}>
                            <Dashboard/>
                        </LoadingContainer>
                    </MainPage>
                </DashboardDispatchContext.Provider>
            </DashboardContext.Provider>
        </>
    )
}
