import {createContext, useContext, useEffect, useState} from "react";
import {DashboardContext} from "@components/dashboards/DashboardContextProvider";
import {Form} from "antd";

export const DashboardWidgetCellContext = createContext({
    // Widget title
    title: "",
    setTitle: (value) => {
    },
    // Extra commands for the widget
    extra: undefined,
    setExtra: (value) => {
    },
    // Expansion state
    expanded: false,
    toggleExpansion: () => {
    },
    // Dashboard edition
    dashboardEdition: false,
    widgetEdition: false,
    widgetSaving: false,
    widgetEditionForm: undefined,
    startWidgetEdition: () => {
    },
    saveWidgetEdition: () => {
    },
    cancelWidgetEdition: () => {
    },
    deleteWidget: () => {
    },
})

export default function DashboardWidgetCellContextProvider({widget, children}) {

    const [title, setTitle] = useState("")
    const [extra, setExtra] = useState()

    const dashboardContext = useContext(DashboardContext)
    const [expanded, setExpanded] = useState(false)
    useEffect(() => {
        setExpanded(!dashboardContext?.edition && dashboardContext?.expandedWidget === widget.uuid)
    }, [dashboardContext?.expandedWidget, dashboardContext?.edition]);
    const toggleExpansion = () => {
        if (dashboardContext.expandedWidget === widget.uuid) {
            dashboardContext.setExpandedWidget(undefined)
        } else {
            dashboardContext.setExpandedWidget(widget.uuid)
        }
    }

    const [widgetEdition, setWidgetEdition] = useState(false)
    const [widgetSaving, setWidgetSaving] = useState(false)
    const [widgetEditionForm] = Form.useForm()

    const startWidgetEdition = () => {
        setWidgetEdition(true)
    }

    const saveWidgetEdition = () => {
        if (widgetEdition) {
            setWidgetSaving(true)
            // See https://ant.design/components/form#forminstance
            widgetEditionForm.validateFields()
                .then(values => {
                    return dashboardContext.saveWidget(widget.uuid, values)
                })
                .then(() => {
                    cancelWidgetEdition()
                })
                .finally(() => {
                    setWidgetSaving(false)
                })
        }
    }

    const cancelWidgetEdition = () => {
        if (widgetEdition) {
            widgetEditionForm.resetFields()
            setWidgetEdition(false)
        }
    }

    const deleteWidget = () => {
        dashboardContext.deleteWidget(widget.uuid)
    }

    const context = {
        title,
        setTitle,
        extra,
        setExtra,
        expanded,
        toggleExpansion,
        dashboardEdition: dashboardContext?.edition,
        widgetEdition,
        widgetSaving,
        widgetEditionForm,
        startWidgetEdition,
        saveWidgetEdition,
        cancelWidgetEdition,
        deleteWidget,
    }

    return (
        <>
            <DashboardWidgetCellContext.Provider value={context}>
                {children}
            </DashboardWidgetCellContext.Provider>
        </>
    )
}