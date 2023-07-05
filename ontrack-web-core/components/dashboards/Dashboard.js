import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Skeleton, Space} from "antd";
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
                            message="Dashboard in edition mode."
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