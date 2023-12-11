import {createContext, useContext, useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {
    deleteDashboardQuery,
    gqlDashboardFragment, loadDashboardsQuery,
    saveDashboardQuery,
    selectDashboardQuery,
    shareDashboardQuery
} from "@components/dashboards/DashboardConstants";
import {Modal} from "antd";
import {GridTableContext} from "@components/grid/GridTableContext";

export const DashboardContext = createContext({
    /**
     * Selected dashboard
     */
    dashboard: {},
    /**
     * Records a nex layout for the widgets
     */
    recordLayout: (layout) => {},
    /**
     * Selecting a dashboard
     */
    selectDashboard: (value) => {
    },
    /**
     * Refreshing the dashboard
     */
    refresh: () => {
    },
    /**
     * Shares the current dashboard
     */
    deleteDashboard: () => {
    },
    /**
     * Shares the current dashboard
     */
    shareDashboard: () => {
    },
    /**
     * Starts the edition of the current dashboard
     */
    startEdition: () => {
    },
    /**
     * Saving the edition
     */
    saveEdition: (layout) => {
    },
    /**
     * Cancelling the edition
     */
    cancelEdition: () => {
    },
    /**
     * Edition state
     */
    edition: false,
    /**
     * Edition being currently saved
     */
    saving: false,
    /**
     * Saves the configuration for a widget
     */
    saveWidget: (uuid, config) => {
    },
    /**
     * Adding a widget
     */
    addWidget: (widget) => {
    },
    /**
     * Deleting a widget
     */
    deleteWidget: (uuid) => {
    },
})

export default function DashboardContextProvider({children}) {

    const client = useGraphQLClient()
    const {clearExpandedId, setExpandable} = useContext(GridTableContext)

    const [dashboard, setDashboard] = useState()
    const [dashboardRefresh, setDashboardRefresh] = useState(0)

    const [edition, setEdition] = useState(false)
    const [saving, setSaving] = useState(false)
    const [copyDashboard, setCopyDashboard] = useState()

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query UserDashboard {
                        userDashboard {
                            ...DashboardData
                        }
                    }

                    ${gqlDashboardFragment}
                `
            ).then(data => {
                setDashboard(data.userDashboard)
            })
        }
    }, [client, dashboardRefresh]);

    const widgetLayout = (layout, uuid) => {
        const item = layout.find(it => it.i === uuid)
        if (item) {
            return {
                x: item.x,
                y: item.y,
                w: item.w,
                h: item.h,
            }
        } else {
            return null
        }
    }

    const recordLayout = (layout) => {
        setDashboard({
            ...dashboard,
            widgets: dashboard.widgets.map(widget => {
                return {
                    ...widget,
                    layout: widgetLayout(layout, widget.uuid) ?? widget.layout,
                }
            }),
        })
    }

    const selectDashboard = (value) => {
        if (dashboard?.uuid !== value.uuid) {
            clearExpandedId()
            setEdition(false)
            setDashboard(value)
            client.request(
                selectDashboardQuery,
                {uuid: value.uuid}
            )
        }
    }

    const refresh = () => {
        setDashboardRefresh(dashboardRefresh + 1)
    }

    const shareDashboard = () => {
        if (dashboard && dashboard.authorizations.share) {
            setDashboard({
                ...dashboard,
                userScope: 'SHARED',
            })
            client.request(
                shareDashboardQuery,
                {uuid: dashboard.uuid}
            )
        }
    }

    const deleteDashboard = () => {
        if (dashboard && dashboard.authorizations.delete) {
            Modal.confirm({
                title: "Deleting a dashboard",
                content: `Do you really want to delete the "${dashboard.name}" dashboard?`,
                okText: "Delete",
                okType: "danger",
                onOk: () => {
                    client.request(
                        deleteDashboardQuery,
                        {uuid: dashboard.uuid}
                    )
                    // Selects the default dashboard
                    refresh()
                }
            })
        }
    }

    const startEdition = () => {
        if (dashboard) {
            // Taking a backup of the current dashboard
            setCopyDashboard({
                ...dashboard,
                widgets: dashboard.widgets.map(widget => ({
                    ...widget,
                }))
            })
            // Stopping any expansion
            clearExpandedId()
            setExpandable(false)
            // Starting the edition
            setEdition(true)
        }
    }

    const cancelEdition = () => {
        if (edition && copyDashboard) {
            setDashboard(copyDashboard)
            setCopyDashboard(null)
            setEdition(false)
            setExpandable(true)
        }
    }

    const saveEdition = (layout) => {
        if (dashboard && edition) {
            setSaving(true)
            client.request(
                saveDashboardQuery,
                dashboard
            ).then(() => {
                // OK
                setEdition(false)
                // Refresh the view
                refresh()
            }).finally(() => {
                setSaving(false)
                setExpandable(true)
            })
        }
        setCopyDashboard(null)
    }

    const addWidget = (widget) => {
        if (dashboard && edition) {
            setDashboard({
                ...dashboard,
                widgets: [
                    ...dashboard.widgets,
                    widget,
                ]
            })
        }
    }

    const saveWidget = (uuid, config) => {
        if (dashboard && edition) {
            setDashboard({
                ...dashboard,
                widgets: dashboard.widgets.map(widget => {
                    if (uuid === widget.uuid) {
                        return {
                            ...widget,
                            config,
                        }
                    } else {
                        return widget
                    }
                })
            })
        }
    }

    const deleteWidget = (uuid) => {
        if (dashboard && edition) {
            setDashboard({
                ...dashboard,
                widgets: dashboard.widgets.filter(it => it.uuid !== uuid)
            })
        }
    }

    const context = {
        dashboard,
        recordLayout,
        selectDashboard,
        refresh,
        shareDashboard,
        deleteDashboard,
        startEdition,
        saveEdition,
        cancelEdition,
        edition,
        saving,
        addWidget,
        saveWidget,
        deleteWidget,
    }

    return (
        <>
            <DashboardContext.Provider value={context}>
                {children}
            </DashboardContext.Provider>
        </>
    )
}
