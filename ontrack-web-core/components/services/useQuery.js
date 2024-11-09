import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {getGraphQLErrors} from "@components/services/graphql-utils";

export const useQuery = (query, {variables} = {}) => {
    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [data, setData] = useState()

    useEffect(() => {
        if (client) {
            const runQuery = async () => {
                setError('')
                setLoading(true)
                try {
                    const data = await client.request(query, variables)
                    const errors = getGraphQLErrors(data)
                    if (errors && errors.length > 0) {
                        setError(errors[0].message)
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
    }, [client])

    return {
        loading,
        error,
        data,
    }
}