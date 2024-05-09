import {Descriptions, Typography} from "antd";

export default function InMemoryNotificationChannelConfig({group, data}) {

    const items = [
        {
            key: 'group',
            label: "Group",
            children: <Typography.Text>{group}</Typography.Text>,
        },
    ]

    if (data) {
        items.push({
            key: 'data',
            label: "Data",
            children: <Typography.Text code>{data}</Typography.Text>,
        })
    }

    return (
        <>
            <Descriptions items={items}/>
        </>
    )
}