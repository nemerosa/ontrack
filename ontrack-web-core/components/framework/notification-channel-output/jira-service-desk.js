import Link from "next/link";
import {Descriptions, Typography} from "antd";
import YesNo from "@components/common/YesNo";
import JiraCustomFields from "@components/extension/jira/JiraCustomFields";

export default function JiraServiceDeskNotificationChannelOutput({

                                                                     serviceDeskId,
                                                                     requestTypeId,
                                                                     existing,
                                                                     fields,
                                                                     ticketKey,
                                                                     url
                                                                 }) {

    const items = [
        {
            key: 'serviceDeskId',
            label: "Service desk ID",
            children: <Typography.Text code>{serviceDeskId}</Typography.Text>,
            span: 6,
        },
        {
            key: 'requestTypeId',
            label: "Request type ID",
            children: <Typography.Text code>{requestTypeId}</Typography.Text>,
            span: 6,
        },
    ]
    if (ticketKey && url) {
        items.push({
            key: 'ticket',
            label: "Ticket",
            children: <Link href={url}>{ticketKey}</Link>,
            span: 6,
        })
    }
    if (existing === true || existing === false) {
        items.push({
            key: 'existing',
            label: "Existing ticket",
            children: <YesNo value={existing}/>,
            span: 6,
        })
    }
    if (fields) {
        items.push({
            key: 'fields',
            label: "Fields",
            children: <JiraCustomFields customFields={fields}/>,
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