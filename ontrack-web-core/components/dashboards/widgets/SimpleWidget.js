import graphQLCall from "@client/graphQLCall";
import Widget from "@components/dashboards/widgets/Widget";
import {useContext, useEffect, useState} from "react";
import {WidgetContext} from "@components/dashboards/widgets/WidgetContext";

export default function SimpleWidget({
                                         title, query, queryDeps = [], variables,
                                         setData,
                                         getCommands,
                                         form,
                                         children
                                     }) {
    const [loading, setLoading] = useState(true)
    const [commands, setCommands] = useState([])

    const widgetContext = useContext(WidgetContext)

    useEffect(() => {
        if (variables) {
            setLoading(true)
            graphQLCall(
                query,
                variables
            ).then(data => {
                setData(data)
                if (getCommands) {
                    setCommands(getCommands(data))
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, queryDeps)

    return (
        <Widget
            title={title}
            loading={loading}
            commands={commands}
            form={form}
        >
            {children}
        </Widget>
    )
}