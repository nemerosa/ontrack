import {Descriptions, Input} from "antd";
import {useEffect, useState} from "react";
import TimestampText from "@components/common/TimestampText";

export default function WebhookExchangeTableDetails({webhookExchange}) {

    const [items, setItems] = useState([])
    useEffect(() => {
        const items = []

        // Request part
        items.push(
            {
                label: 'Request timestamp',
                children: <TimestampText value={webhookExchange.request?.timestamp}/>,
                span: 6,
            },
            {
                label: 'Request type',
                children: webhookExchange.request?.type,
                span: 6,
            },
            {
                label: 'Request payload',
                children: <Input.TextArea
                    readOnly
                    value={webhookExchange.request?.payload}
                    rows={6}
                />,
                span: 12,
            },
        )

        // Response part
        items.push(
            {
                label: 'Response timestamp',
                children: <TimestampText value={webhookExchange.response?.timestamp}/>,
                span: 6,
            },
            {
                label: 'Response code',
                children: webhookExchange.response?.code,
                span: 6,
            },
            {
                label: 'Response payload',
                children: <Input.TextArea
                    readOnly
                    value={webhookExchange.response?.payload}
                    rows={6}
                />,
                span: 12,
            },
        )

        setItems(items)
    }, [webhookExchange])

    return (
        <>
            <Descriptions items={items} column={12}/>
        </>
    )
}