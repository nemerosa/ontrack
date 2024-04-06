import {useEffect, useState} from "react";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {Select, Space, Tag, Typography} from "antd";

export default function SelectNotificationChannel({value, onChange, onSelectedNotificationChannel}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [options, setOptions] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query GetNotificationChannels {
                        notificationChannels {
                            type
                            enabled
                        }
                    }
                `
            ).then(data => {
                const channels = data.notificationChannels
                setOptions(channels.map((channel, index) => ({
                    value: channel.type,
                    label: <Space>
                        <Tag>{channel.type}</Tag>
                        {
                            !channel.enabled && <Typography.Text type="secondary">(disabled)</Typography.Text>
                        }
                    </Space>
                })))
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client])

    const onLocalChange = (value) => {
        if (onChange) onChange(value)
        if (onSelectedNotificationChannel) {
            onSelectedNotificationChannel(value)
        }
    }

    return (
        <>
            <Select
                options={options}
                loading={loading}
                value={value}
                onChange={onLocalChange}
            />
        </>
    )

}