import {Descriptions} from "antd";
import {useEffect, useState} from "react";
import JsonDisplay from "@components/common/JsonDisplay";

export default function GitHubIngestionHookPayloadDetails({record}) {

    const [items, setItems] = useState([])

    useEffect(() => {
        const items = []

        if (record.outcomeDetails) {
            items.push({
                label: 'Outcome details',
                children: record.outcomeDetails,
                span: 12,
            })
        }

        if (record.payload) {
            items.push({
                label: 'Payload',
                children: <JsonDisplay value={JSON.stringify(record.payload, null, 2)}/>,
                span: 12,
            })
        }

        setItems(items)
    }, [record])

    return (
        <>
            <Descriptions
                column={12}
                items={items}
            />
        </>
    )
}