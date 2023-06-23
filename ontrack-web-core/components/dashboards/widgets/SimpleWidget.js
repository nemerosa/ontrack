import graphQLCall from "@client/graphQLCall";
import Widget from "@components/dashboards/widgets/Widget";
import {useEffect, useState} from "react";

export default function SimpleWidget({title, query, variables, setData, children}) {
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        if (variables) {
            setLoading(true)
            graphQLCall(
                query,
                variables
            ).then(data => {
                setData(data)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [])

    return (
        <Widget title={title} loading={loading}>
            {children}
        </Widget>
    )
}