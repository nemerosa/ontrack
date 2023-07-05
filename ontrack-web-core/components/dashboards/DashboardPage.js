import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import {useEffect, useReducer} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {SaveDashboardDialog, useSaveDashboardDialog} from "@components/dashboards/SaveDashboardDialog";
import {saveDashboardQuery} from "@components/dashboards/DashboardConstants";

export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    const saveDashboardDialog = useSaveDashboardDialog()

    const createDashboardCopy = (dashboard, action) => {
        return {
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
            )),
            message: action.message,
        }
    }

    const dashboardSaveAs = (dashboard, action) => {
        // Copies the current dashboard
        const copy = createDashboardCopy(dashboard, action)
        saveDashboardDialog.start(copy)
        return dashboard // Not saved immediately
    }

    const dashboardSave = (dashboard, action) => {
        const copy = createDashboardCopy(dashboard, action)
        // TODO graphQLCall(
        //     saveDashboardQuery,
        //     {
        //         context: copy.context,
        //         contextId: copy.contextId,
        //     }
        // )
    }

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
            case 'save': {
                if (!dashboard.key) {
                    return dashboardSaveAs(dashboard, action)
                } else {
                    return dashboardSave(dashboard, action)
                }
            }
            case 'save-as': {
                dashboardSaveAs(dashboard, action)
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
                    <SaveDashboardDialog saveDashboardDialog={saveDashboardDialog}/>
                </DashboardDispatchContext.Provider>
            </DashboardContext.Provider>
        </>
    )
}
