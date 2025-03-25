import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {getGraphQLErrors} from "@components/services/graphql-utils";
import {useReloadState} from "@components/common/StateUtils";

export const useQuery = (query, {
    variables,
    skipInitialFetch = false,
    initialData,
    condition = true,
    deps = [],
    dataFn
} = {}) => {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(!skipInitialFetch)
    const [error, setError] = useState('')
    const [data, setData] = useState(initialData)

    const [reloadState, reload] = useReloadState()

    useEffect(() => {
        if (client && condition && (reloadState > 0 || !skipInitialFetch)) {
            const runQuery = async () => {
                setError('')
                setLoading(true)
                try {
                    const data = await client.request(query, variables)
                    const errors = getGraphQLErrors(data)
                    if (errors && errors.length > 0) {
                        setError(errors[0].message)
                    } else if (dataFn) {
                        setData(dataFn(data))
                    } else {
                        setData(data)
                    }
                } finally {
                    setLoading(false)
                }
            }
            // noinspection JSIgnoredPromiseFromCall
            runQuery()
        }
    }, [client, condition, reloadState, ...deps])

    return {
        loading,
        error,
        data,
        setData,
        refetch: reload,
    }
}