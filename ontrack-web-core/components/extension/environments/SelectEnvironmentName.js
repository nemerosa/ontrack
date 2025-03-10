import {Select} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export default function SelectEnvironmentName({id = "environment", projects = [], value, onChange}) {

    const client = useGraphQLClient()

    const [environments, setEnvironments] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query SelectEnvironments(
                        $projects: [String!],
                    ) {
                        environments(filter: {projects: $projects}) {
                            name
                        }
                    }
                `,
                {projects}
            ).then(data => {
                setEnvironments(data.environments.map(env => ({
                    value: env.name,
                    label: env.name,
                })))
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client])

    return (
        <>
            <Select
                id={id}
                data-testid={id}
                optionFilterProp="label"
                placeholder="Select environment"
                options={environments}
                loading={loading}
                value={value}
                allowClear
                onChange={onChange}
            />
        </>
    )
}