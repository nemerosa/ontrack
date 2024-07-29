import {Select, Space, Typography} from "antd";
import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export default function SelectIssueService({value, onChange, self}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(false)
    const [options, setOptions] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query GetIssueServicesConfigurations {
                        issueServiceConfigurations {
                            name
                            id
                        }
                    }
                `
            ).then(data => {
                const options = data.issueServiceConfigurations.map(({name, id}) => ({
                    value: id,
                    label: <Space>
                        <Typography.Text>{name}</Typography.Text>
                        <Typography.Text type="secondary">[{id}]</Typography.Text>
                    </Space>,
                }))
                if (self) {
                    options.push({
                        value: "self",
                        label: <Space>
                            <Typography.Text>{self}</Typography.Text>
                            <Typography.Text type="secondary">[self]</Typography.Text>
                        </Space>,
                    })
                }
                setOptions(options)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, self])

    return (
        <>
            <Select
                options={options}
                loading={loading}
                value={value}
                onChange={onChange}
                allowClear={true}
            />
        </>
    )
}