import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Skeleton, Space, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function Dashboard() {

    const {selectedDashboard} = useContext(DashboardContext)
    // const dashboardDispatch = useContext(DashboardDispatchContext)
    //
    // const onStopEdition = () => {
    //     dashboardDispatch({
    //         type: 'cancelEdition',
    //     })
    // }

    const importLayout = layoutKey => lazy(() =>
        import(`./layouts/${layoutKey}Layout`)
    )

    const [loadedLayout, setLoadedLayout] = useState(undefined)

    useEffect(() => {
        if (selectedDashboard?.layoutKey) {
            const loadLayout = async () => {
                const Layout = await importLayout(selectedDashboard.layoutKey)
                setLoadedLayout(<Layout/>)
            }
            loadLayout().then(() => {
            })
        }
    }, [selectedDashboard])

    return (
        <>
            {
                selectedDashboard && <Suspense fallback={<Skeleton active/>}>
                    <Space direction="vertical" style={{
                        width: '100%'
                    }}>
                        <div>
                            <LayoutContextProvider>
                                {loadedLayout}
                            </LayoutContextProvider>
                        </div>
                    </Space>
                </Suspense>
            }
        </>
    )

}