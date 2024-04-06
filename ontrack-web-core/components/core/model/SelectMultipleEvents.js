import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {Select, Space, Tag} from "antd";

export default function SelectMultipleEvents({value, onChange}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [eventTypes, setEventTypes] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query Events {
                        eventTypes {
                            value: id
                            label: description
                        }
                    }
                `
            ).then(data => {
                setEventTypes(data.eventTypes)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client]);

    return (
        <>
            <Select
                loading={loading}
                options={eventTypes}
                allowClear={true}
                mode="multiple"
                value={value}
                onChange={onChange}
                optionRender={(option) =>
                    <Space>
                        <Tag>{option.value}</Tag>
                        {option.label}
                    </Space>
                }
            />
        </>
    )
}