import graphQLCall from "@client/graphQLCall";
import Widget from "@components/dashboards/widgets/Widget";
import {useEffect, useState} from "react";

export default function SimpleWidget({
                                         title, query, queryDeps = [], variables,
                                         setData,
                                         getCommands,
                                         children
                                     }) {
    const [loading, setLoading] = useState(true)
    const [commands, setCommands] = useState([])

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
        >
            {children}
        </Widget>
    )
}