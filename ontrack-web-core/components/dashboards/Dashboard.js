import {lazy, useEffect, useState, Suspense} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {Alert, Skeleton, Space} from "antd";

export default function Dashboard({
                                      context, contextId = "-",
                                      editionMode
                                  }) {

    const [dashboard, setDashboard] = useState({})
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
                console.log({dashboard: data.dashboardByContext})
                setDashboard(data.dashboardByContext)
            })
        }
    }, [context, contextId])

    const importLayout = layoutKey => lazy(() =>
        import(`./layouts/${layoutKey}Layout`)
    )

    const [loadedLayout, setLoadedLayout] = useState(undefined)

    useEffect(() => {
        if (dashboard?.layoutKey) {
            const loadLayout = async () => {
                const Layout = await importLayout(dashboard.layoutKey)
                setLoadedLayout(<Layout widgets={dashboard.widgets} context={context} contextId={contextId}/>)
            }
            loadLayout().then(() => {
            })
        }
    }, [dashboard, context, contextId])

    return (
        <>
            {/* TODO Loading indicator for the dashboard */}
            {dashboard && <Suspense fallback={<Skeleton active/>}>
                <Space direction="vertical">
                    {
                        editionMode &&
                        <Alert type="warning" message="Dashboard in edition mode."/>
                    }
                    <div>{loadedLayout}</div>
                </Space>
            </Suspense>}
        </>
    )

}