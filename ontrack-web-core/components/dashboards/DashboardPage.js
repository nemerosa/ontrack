import MainPage from "@components/layouts/MainPage";
import Dashboard from "@components/dashboards/Dashboard";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";
import {DashboardContext} from "@components/dashboards/DashboardContext";
import {useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";

export default function DashboardPage({
                                          title,
                                      }) {

    const [dashboards, setDashboards] = useState([])
    const [selectedDashboard, setSelectedDashboard] = useState(undefined)

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
            setSelectedDashboard(data.userDashboard)
        })
    }, [])

    const commands = [
        <DashboardCommandMenu/>
    ]

    return (
        <>
            <DashboardContext.Provider value={{dashboards, selectedDashboard}}>
                <MainPage
                    title={title}
                    breadcrumbs={[]}
                    commands={commands}
                >
                    <Dashboard/>
                </MainPage>
            </DashboardContext.Provider>
        </>
    )
}
