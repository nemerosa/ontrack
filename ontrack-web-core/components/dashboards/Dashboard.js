import {lazy, useEffect, useState, Suspense} from "react";
import graphQLCall from "@client/graphQLCall";
import {gql} from "graphql-request";
import {Alert, Button, Skeleton, Space} from "antd";

export default function Dashboard({
                                      context, contextId = "-",
                                      editionMode, onStopEdition
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
                setLoadedLayout(
                    <Layout
                        widgets={dashboard.widgets}
                        context={context}
                        contextId={contextId}
                        editionMode={editionMode}
                    />
                )
            }
            loadLayout().then(() => {
            })
        }
    }, [dashboard, context, contextId, editionMode])

    return (
        <>
            {dashboard && <Suspense fallback={<Skeleton active/>}>
                <Space direction="vertical">
                    {
                        editionMode &&
                        <Alert
                            type="warning"
                            message="Dashboard in edition mode."
                            action={
                                <Button size="small" danger onClick={onStopEdition}>Close edition</Button>
                            }
                        />
                    }
                    <div>{loadedLayout}</div>
                </Space>
            </Suspense>}
        </>
    )

}