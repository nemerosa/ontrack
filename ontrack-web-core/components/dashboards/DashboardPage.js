import MainPage from "@components/layouts/MainPage";
import {CloseCommand} from "@components/common/Commands";
import LoadingContainer from "@components/common/LoadingContainer";
import Dashboard from "@components/dashboards/Dashboard";
import {createContext, useEffect, useState} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import DashboardCommandMenu from "@components/dashboards/commands/DashboardCommandMenu";

export const DashboardContext = createContext(null)
export default function DashboardPage({
                                          title, breadcrumbs, closeHref,
                                          loading,
                                          context, contextId
                                      }) {

    // const [editionMode, setEditionMode] = useState(false)

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
                setDashboard({
                    context,
                    contextId,
                    ...data.dashboardByContext
                })
            })
        }
    }, [context, contextId])

    const commands = [
        <DashboardCommandMenu/>,
        <CloseCommand key="close" href={closeHref}/>,
    ]

    return (
        <>
            <MainPage
                title={title}
                breadcrumbs={breadcrumbs}
                commands={commands}
            >
                <LoadingContainer loading={loading}>
                    <DashboardContext.Provider value={dashboard}>
                        <Dashboard/>
                    </DashboardContext.Provider>
                </LoadingContainer>
            </MainPage>
        </>
    )
}