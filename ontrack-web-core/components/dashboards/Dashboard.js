import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Alert, Button, Skeleton, Space, Typography} from "antd";
import LayoutContextProvider from "@components/dashboards/layouts/LayoutContext";
import {DashboardContext, DashboardDispatchContext} from "@components/dashboards/DashboardContext";

export default function Dashboard() {

    const {selectedDashboard} = useContext(DashboardContext)
    const selectedDashboardDispatch = useContext(DashboardDispatchContext)

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

    const onStopEdition = () => {
        selectedDashboardDispatch({type: 'stopEdition'})
    }

    return (
        <>
            {
                selectedDashboard && <Suspense fallback={<Skeleton active/>}>
                    <Space direction="vertical" style={{
                        width: '100%'
                    }}>
                        {
                            selectedDashboard.editionMode &&
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
                </Suspense>
            }
        </>
    )

}