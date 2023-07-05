import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import {useEffect, useReducer} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {
    const dashboardReducer = (dashboard, action) => {
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
            case 'save-as': {
                // Copies the current dashboard
                const copy = {
                    context: dashboard.context,
                    contextId: dashboard.contextId,
                    key: '', // Marking as new
                    name: `Copy of ${dashboard.name}`,
                    builtIn: false,
                    layoutKey: dashboard.layoutKey,
                    widgets: dashboard.widgets.map(widget => (
                        {
                            uuid: '', // Marking as new
                            key: widget.key,
                            config: action.widget && action.widget.uuid === widget.uuid ? action.widget.config : widget.config,
                        }
                    ))
                }
                console.log({
                    dashboard,
                    copy,
                })
                // dashboard.dashboardSaveAs(copy)
                return dashboard // No change for now, just launches the save process
            }
            default: {
                throw Error('Unknown action: ' + action.type);
            }
        }
    }

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
