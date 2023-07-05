import {lazy, Suspense, useContext, useEffect, useReducer, useState} from "react";
import {Skeleton} from "antd";
import {DashboardContext} from "@components/dashboards/DashboardContext";
import {dashboardReducer} from "@components/dashboards/DashboardPage";
import {WidgetContext, WidgetDispatchContext, widgetReducer} from "@components/dashboards/widgets/WidgetContext";

export default function DashboardWidget({widget}) {

    const dashboard = useContext(DashboardContext)

    const [widgetContext, widgetDispatch] = useReducer(widgetReducer, {})
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
                <WidgetContext.Provider value={widgetContext}>
                    <WidgetDispatchContext.Provider value={widgetDispatch}>
                        {loadedWidget}
                    </WidgetDispatchContext.Provider>
                </WidgetContext.Provider>
            </Suspense>}
        </div>
    )
}