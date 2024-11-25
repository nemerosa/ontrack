import {Select} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {gqlEnvironmentData} from "@components/extension/environments/EnvironmentGraphQL";

export default function SelectEnvironmentIds({id = "environments", value, onChange}) {

    const client = useGraphQLClient()

    const [environments, setEnvironments] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query SelectEnvironments {
                        environments {
                            ...EnvironmentData
                        }
                    }
                    ${gqlEnvironmentData}
                `
            ).then(data => {
                setEnvironments(data.environments.map(env => ({
                    value: env.id,
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
                mode="multiple"
                optionFilterProp="label"
                placeholder="Select environments"
                options={environments}
                loading={loading}
                value={value}
                allowClear
                onChange={onChange}
            />
        </>
    )
}