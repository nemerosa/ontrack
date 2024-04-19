import Link from "next/link";
import {Descriptions, Input, Tag, Typography} from "antd";
import YesNo from "@components/common/YesNo";
import JiraCustomFields from "@components/extension/jira/JiraCustomFields";

export default function JiraCreationNotificationChannelOutput({
                                                                  title,
                                                                  labels,
                                                                  jql,
                                                                  existing,
                                                                  fixVersion,
                                                                  customFields,
                                                                  body,
                                                                  ticketKey,
                                                                  url
                                                              }) {

    const items = []
    if (title) {
        items.push({
            key: 'title',
            label: "Title",
            children: <Typography.Text>{title}</Typography.Text>,
            span: 12,
        })
    }
    if (labels) {
        items.push({
            key: 'labels',
            label: "Labels",
            children: labels.map(label => <Tag key={label}>{label}</Tag>),
            span: 12,
        })
    }
    if (jql) {
        items.push({
            key: 'jql',
            label: "JQL",
            children: <Typography.Text code>{jql}</Typography.Text>,
            span: 12,
        })
    }
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
    if (fixVersion) {
        items.push({
            key: 'fixVersion',
            label: "Fix version",
            children: <Typography.Text>{fixVersion}</Typography.Text>,
            span: 6,
        })
    }
    if (customFields) {
        items.push({
            key: 'customFields',
            label: "Custom fields",
            children: <JiraCustomFields customFields={customFields}/>,
            span: 12,
        })
    }
    if (body) {
        items.push({
            key: 'body',
            label: "Body",
            children: <Input.TextArea disabled rows={6} style={{width: '30em'}} value={body}/>,
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