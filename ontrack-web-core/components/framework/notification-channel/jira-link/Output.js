import {Descriptions, Typography} from "antd";

export default function JiraLinkNotificationChannelOutput({
                                                              sourceTicket,
                                                              targetTicket,
                                                          }) {

    const items = []

    if (sourceTicket) {
        items.push({
            key: 'sourceTicket',
            label: "Source ticket",
            children: <Typography.Text>{sourceTicket}</Typography.Text>,
            span: 12,
        })
    }

    if (targetTicket) {
        items.push({
            key: 'targetTicket',
            label: "Target ticket",
            children: <Typography.Text>{targetTicket}</Typography.Text>,
            span: 12,
        })
    }

    return (
        <>
            <Descriptions
                column={12}
                items={items}
            />
        </>
    )
}