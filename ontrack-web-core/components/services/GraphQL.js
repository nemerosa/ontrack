"use client"

import {useEffect, useState} from "react";

const createLowLevelError = (ex) => {
    // TODO
    return ex
}

export const useQuery = (query, {
    variables = {},
    deps = [],
    initialData = null,
    condition = true,
    dataFn = (data) => data,
} = {}) => {
    const [data, setData] = useState(initialData)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState()
    const [finished, setFinished] = useState(false)

    useEffect(() => {
        if (condition) {
            setLoading(true)
            callGraphQL({query, variables})
                .then(data => {
                    setData(dataFn(data))
                    setError(null)
                })
                .catch((ex) => {
                    setError(createLowLevelError(ex))
                    setData(null)
                })
                .finally(() => {
                    setFinished(true)
                    setLoading(false)
                })
        }
    }, [condition, ...deps])

    return {
        data,
        loading,
        error,
        finished,
    }
}

export const useMutation = (query, {userNodeName, onSuccess}) => {
    const [data, setData] = useState({})
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState()

    const mutate = async (variables) => {
        setLoading(true)
        try {
            const data = await callGraphQL({query, variables})
            const userNode = data[userNodeName]
            if (userNode) {
                setData(userNode)
                const errors = userNode.errors
                if (errors && errors.length > 0) {
                    throw new Error("TODO Management of GraphQL errors")
                    // setError(
                    //     createCodeError(code, parameters)
                    // )
                } else if (onSuccess) {
                    onSuccess(userNode)
                }
            }
        } finally {
            setLoading(false)
        }
    }

    return {
        mutate,
        data,
        loading,
        error,
    }
}

export async function callGraphQL({
                                      query,
                                      variables,
                                  }) {
    const res = await fetch('/api/protected/graphql', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            query,
            variables,
        })
    })
    if (res.ok) {
        return await res.json()
    } else {
        console.error(res)
        throw new Error("Issue with GraphQL call.");
    }
}
