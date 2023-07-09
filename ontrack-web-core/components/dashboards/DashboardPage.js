import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";
import {useEffect, useReducer, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import CloneDashboardDialog, {useCloneDashboardDialog} from "@components/dashboards/commands/CloneDashboardDialog";

export default function DashboardPage({
                                          title,
                                      }) {

    const cloneDashboardDialog = useCloneDashboardDialog({
        onSuccess: (result) => {
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
                selectedDashboardDispatch({type: 'init', selectedDashboard: result.dashboard})
            })
        }
    })
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
                cloneDashboardDialog.start(copy)
                // Not changing the selected dashboard for now, just opening the dialog
                return selectedDashboard
            }
            case 'edit': {
                return {
                    ...selectedDashboard,
                    editionMode: true,
                }
            }
            case 'stopEdition': {
                return {
                    ...selectedDashboard,
                    editionMode: false
                }
            }
        }
    }

    const [dashboards, setDashboards] = useState([])
    const [selectedDashboard, selectedDashboardDispatch] = useReducer(selectedDashboardReducer, undefined)

    useEffect(() => {
        graphQLCall(
            gql`
                query LoadDashboards {
                    userDashboards {
                        ...DashboardContent
                    }
                    userDashboard {
                        ...DashboardContent
                    }
                }

                fragment DashboardContent on Dashboard {
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
            `
        ).then(data => {
            setDashboards(data.userDashboards)
            selectedDashboardDispatch({
                type: 'init',
                selectedDashboard: data.userDashboard,
            })
        })
    }, [])

    const commands = [
        <DashboardCommandMenu/>
    ]

    return (
        <>
            <DashboardContext.Provider value={{dashboards, selectedDashboard}}>
                <DashboardDispatchContext.Provider value={selectedDashboardDispatch}>
                    <CloneDashboardDialog cloneDashboardDialog={cloneDashboardDialog}/>
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
