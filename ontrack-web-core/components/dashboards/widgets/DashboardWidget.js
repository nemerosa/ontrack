import {lazy, Suspense, useEffect, useState} from "react";

export default function DashboardWidget({widget, context, contextId}) {
    const importWidget = widgetKey => lazy(() =>
        import(`./${widgetKey}Widget`)
    )

    const [loadedWidget, setLoadedWidget] = useState(undefined)

    useEffect(() => {
        if (widget) {
            const loadWidget = async () => {
                const LoadedWidget = await importWidget(widget.key)
                setLoadedWidget(<LoadedWidget {...widget.config} context={context} contextId={contextId}/>)
            }
            loadWidget().then(() => {
            })
        }
    }, [widget, context, contextId])

    return (
        <div style={{
            width: '100%',
        }}>
            {/* TODO Loading indicator for the widget */}
            {widget && <Suspense fallback={"Loading..."}>
                {loadedWidget}
            </Suspense>}
        </div>
    )
}