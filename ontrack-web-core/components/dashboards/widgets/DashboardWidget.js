import {lazy, Suspense, useEffect, useState} from "react";
import {Skeleton} from "antd";

export default function DashboardWidget({widget}) {
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
            {widget && <Suspense fallback={<Skeleton active/>}>
                {loadedWidget}
            </Suspense>}
        </div>
    )
}