import {lazy, Suspense, useContext, useEffect, useState} from "react";
import {Skeleton} from "antd";
import {DashboardContext} from "@components/dashboards/DashboardPage";

export default function DashboardWidget({widget}) {

    const dashboard = useContext(DashboardContext)
    const importWidget = widgetKey => lazy(() =>
        import(`./${widgetKey}Widget`)
    )

    const [loadedWidget, setLoadedWidget] = useState(undefined)

    useEffect(() => {
        if (widget) {
            const loadWidget = async () => {
                const LoadedWidget = await importWidget(widget.key)
                setLoadedWidget(
                    <LoadedWidget{...widget.config}/>
                )
            }
            loadWidget().then(() => {
            })
        }
    }, [widget])

    return (
        <div style={{
            width: '100%',
        }}>
            {dashboard && widget && <Suspense fallback={<Skeleton active/>}>
                {loadedWidget}
            </Suspense>}
        </div>
    )
}