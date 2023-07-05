import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Skeleton, Space, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function Dashboard() {

    const dashboard = useContext(DashboardContext)
    const dashboardDispatch = useContext(DashboardDispatchContext)

    const onStopEdition = () => {
        dashboardDispatch({
            type: 'cancelEdition',
        })
    }

    const importLayout = layoutKey => lazy(() =>
        import(`./layouts/${layoutKey}Layout`)
    )

    const [loadedLayout, setLoadedLayout] = useState(undefined)

    useEffect(() => {
        if (dashboard?.layoutKey) {
            const loadLayout = async () => {
                const Layout = await importLayout(dashboard.layoutKey)
                setLoadedLayout(<Layout/>)
            }
            loadLayout().then(() => {
            })
        }
    }, [dashboard])

    return (
        <>
            {dashboard && <Suspense fallback={<Skeleton active/>}>
                <Space direction="vertical">
                    {
                        dashboard.editionMode &&
                        <Alert
                            type="warning"
                            message={
                                <Space direction="vertical" size={8}>
                                    <Typography.Text>Dashboard in edition mode.</Typography.Text>
                                    {dashboard.builtIn &&
                                        <Typography.Text strong>
                                            You're editing a default built-in dashboard. To save your updates,
                                            you'll need to save it with another name.
                                        </Typography.Text>
                                    }
                                </Space>
                            }
                            action={
                                <Button size="small" danger onClick={onStopEdition}>Close edition</Button>
                            }
                        />
                    }
                    <div>
                        <LayoutContextProvider>
                            {loadedLayout}
                        </LayoutContextProvider>
                    </div>
                </Space>
            </Suspense>}
        </>
    )

}