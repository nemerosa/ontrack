import {lazy, useEffect, useState, Suspense} from "react";

export default function Dashboard({context, contextId}) {

    const [dashboard, setDashboard] = useState({})
    useEffect(() => {
        // TODO Call the dashboard API
        setDashboard({
            name: "Default home dashboard",
            layout: {
                key: "Default"
            },
            widgets: [
                {
                    key: "home/LastActiveProjects",
                    config: {
                        count: 20
                    }
                }
            ]
        })
    }, [context, contextId])

    const importLayout = layoutKey => lazy(() =>
        import(`./layouts/${layoutKey}Layout`)
    )

    const [loadedLayout, setLoadedLayout] = useState(undefined)

    useEffect(() => {
        if (dashboard?.layout) {
            const loadLayout = async () => {
                const Layout = await importLayout(dashboard.layout.key)
                setLoadedLayout(<Layout widgets={dashboard.widgets}/>)
            }
            loadLayout().then(() => {})
        }
    }, [dashboard])

    return (
        <>
            {/* TODO Loading indicator for the dashboard */}
            {dashboard && <Suspense fallback={"Loading..."}>
                <div>{loadedLayout}</div>
            </Suspense>}
        </>
    )

}