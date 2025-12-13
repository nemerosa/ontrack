import {Descriptions, Input} from "antd";
import {useEffect, useState} from "react";
import HookRecordInfoLink from "@components/extension/hook/HookRecordInfoLink";

export function HookRecordDetails({record}) {

    const [items, setItems] = useState([])

    useEffect(() => {
        const items = []

        // Request part
        items.push(
            {
                label: 'Request payload',
                children: <Input.TextArea
                    readOnly
                    value={record.request?.body}
                    rows={6}
                />,
                span: 12,
            },
            {
                label: 'Request parameters',
                children: <Descriptions
                    column={12}
                    size="small"
                    items={
                        record.request?.parameters?.map(parameter => {
                            return {
                                label: parameter.name,
                                children: parameter.value,
                                span: 6,
                            }
                        })
                    }
                />,
                span: 12,
            }
        )

        // General
        items.push(
            {
                label: 'Message',
                children: record.message,
                span: 12,
            },
            {
                label: 'Exception',
                children: record.exception,
                span: 12,
            },
        )

        // Response
        items.push(
            {
                label: 'Response type',
                children: record.response?.type,
                span: 12,
            },
            {
                label: 'Response info link',
                children: <HookRecordInfoLink infoLink={record.response?.infoLink}/>,
                span: 12,
            },
        )

        setItems(items)
    }, [record])

    return (
        <>
            <Descriptions items={items} column={12}/>
        </>
    )
}