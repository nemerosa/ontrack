import Widget from "@components/dashboards/widgets/Widget";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";

export default function SimpleWidget({
                                         title, query, queryDeps = [], variables, canQuery,
                                         setData,
                                         getCommands,
                                         form,
                                         padding,
                                         children
                                     }) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [commands, setCommands] = useState([])

    useEffect(() => {
        if (client && variables && (!canQuery || canQuery())) {
            setLoading(true)
            client.request(
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
    }, [client, ...queryDeps])

    return (
        <Widget
            title={title}
            loading={loading}
            commands={commands}
            form={form}
            padding={padding}
        >
            {children}
        </Widget>
    )
}