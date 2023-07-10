import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {useEffect, useReducer, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import SaveDashboardDialog, {useSaveDashboardDialog} from "@components/dashboards/commands/SaveDashboardDialog";
import {
    deleteDashboardQuery,
    loadDashboardsQuery,
    saveDashboardQuery,
    shareDashboardQuery
} from "@components/dashboards/DashboardConstants";
import {Modal} from "antd";

export default function DashboardPage({
                                          title,
                                      }) {

    const reloadDashboards = (newSelection) => {
        graphQLCall(gql`
            query ReloadDashboards {
                userDashboards {
                    uuid
                    name
                    userScope
                    layoutKey
                    widgets {
                        uuid
                        key
                        config
                    }
                }
            }
        `).then(data => {
            setDashboards(data.userDashboards)
            selectedDashboardDispatch({
                type: 'init',
                selectedDashboard: {
                    ...newSelection,
                    editionMode: false,
                }
            })
        })
    }

    const saveDashboardDialog = useSaveDashboardDialog({
        onSuccess: (result) => {
            reloadDashboards(result.dashboard)
        }
    })

    const [copyEditionDashboard, setCopyEditionDashboard] = useState({})

    const selectedDashboardReducer = (selectedDashboard, action) => {
        switch (action.type) {
            case 'init': {
                return action.selectedDashboard
            }
            case 'clone': {
                const copy = {
                    ...selectedDashboard,
                    uuid: '',
                    name: `Copy of ${selectedDashboard.name}`,
                    widgets: selectedDashboard.widgets.map(widget => ({
                        ...widget,
                        uuid: ''
                    }))
                }
                // Opening the dialog
                saveDashboardDialog.start(copy)
                // Not changing the selected dashboard for now, just opening the dialog
                return selectedDashboard
            }
            case 'share': {
                graphQLCall(shareDashboardQuery, {
                    uuid: selectedDashboard.uuid,
                }).then(data => {
                    reloadDashboards(data.shareDashboard.dashboard)
                })
                return selectedDashboard
            }
            case 'delete': {
                Modal.confirm({
                    title: "Deleting a dashboard",
                    content: `Do you really want to delete the "${selectedDashboard.name}" dashboard?`,
                    okText: "Delete",
                    okType: "danger",
                    onOk: () => {
                        return graphQLCall(deleteDashboardQuery, {
                            uuid: selectedDashboard.uuid,
                        }).then(() => {
                            return graphQLCall(loadDashboardsQuery)
                        }).then(data => {
                            setDashboards(data.userDashboards)
                            selectedDashboardDispatch({
                                type: 'init',
                                selectedDashboard: data.userDashboard,
                            })
                        })
                    }
                })
                return selectedDashboard
            }
            case 'create': {
                setCopyEditionDashboard({
                    ...selectedDashboard,
                    editionMode: false,
                    widgets: selectedDashboard.widgets.map(widget => ({
                        ...widget,
                        editionMode: false,
                    }))
                })
                return {
                    uuid: '',
                    name: `New dashboard #${dashboards.length + 1}`,
                    userScope: 'PRIVATE',
                    layoutKey: 'Default',
                    widgets: [],
                    editionMode: true,
                }
            }
            case 'edit': {
                setCopyEditionDashboard({
                    ...selectedDashboard,
                    editionMode: false,
                    widgets: selectedDashboard.widgets.map(widget => ({
                        ...widget,
                        editionMode: false,
                    }))
                })
                return {
                    ...selectedDashboard,
                    editionMode: true,
                }
            }
            case 'stopEdition': {
                return {
                    ...copyEditionDashboard,
                    editionMode: false,
                    widgets: copyEditionDashboard.widgets.map(widget => ({
                        ...widget,
                        editionMode: false,
                    }))
                }
            }
            case 'changeLayout': {
                return {
                    ...selectedDashboard,
                    layoutKey: action.layoutKey,
                }
            }
            case 'saveEdition': {
                if (selectedDashboard.uuid) {
                    graphQLCall(saveDashboardQuery, {
                        uuid: selectedDashboard.uuid,
                        name: selectedDashboard.name,
                        userScope: selectedDashboard.userScope,
                        layoutKey: selectedDashboard.layoutKey,
                        widgets: selectedDashboard.widgets,
                    }).then(() => {
                        reloadDashboards(selectedDashboard)
                    })
                } else {
                    saveDashboardDialog.start(selectedDashboard)
                }
                return selectedDashboard
            }
            case 'saveWidgetConfig': {
                return {
                    ...selectedDashboard,
                    widgets: selectedDashboard.widgets.map(widget => {
                        if (widget.uuid === action.widget.uuid) {
                            return {
                                ...widget,
                                config: action.config,
                            }
                        } else {
                            return widget
                        }
                    })
                }
            }
            case 'addWidget': {
                return {
                    ...selectedDashboard,
                    widgets: selectedDashboard.widgets.concat({
                        uuid: '',
                        key: action.widgetDef.key,
                        config: action.widgetDef.defaultConfig,
                    })
                }
            }
            case 'deleteWidget': {
                return {
                    ...selectedDashboard,
                    widgets: selectedDashboard.widgets.filter(widget => widget.uuid !== action.widgetUuid)
                }
            }
        }
    }

    const [dashboards, setDashboards] = useState([])
    const [selectedDashboard, selectedDashboardDispatch] = useReducer(selectedDashboardReducer, undefined)

    useEffect(() => {
        graphQLCall(
            loadDashboardsQuery
        ).then(data => {
            setDashboards(data.userDashboards)
            selectedDashboardDispatch({
                type: 'init',
                selectedDashboard: data.userDashboard,
            })
        })
    }, [])

    const commands = [
        <DashboardCommandMenu key="dashboard"/>
    ]

    return (
        <>
            <DashboardContext.Provider value={{dashboards, selectedDashboard}}>
                <DashboardDispatchContext.Provider value={selectedDashboardDispatch}>
                    <SaveDashboardDialog saveDashboardDialog={saveDashboardDialog}/>
                    <MainPage
                        title={title}
                        breadcrumbs={[]}
                        commands={commands}
                    >
                        <Dashboard/>
                    </MainPage>
                </DashboardDispatchContext.Provider>
            </DashboardContext.Provider>
        </>
    )
}
