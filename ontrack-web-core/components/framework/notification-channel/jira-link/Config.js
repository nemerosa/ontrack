import {Descriptions, Space, Tag, Typography} from "antd";
import Link from "next/link";
import {useJiraConfigurationUrl} from "@components/extension/jira/Utils";

export default function JiraLinkNotificationChannelConfig({
                                                              configName,
                                                              sourceQuery,
                                                              targetQuery,
                                                              linkName,
                                                          }) {

    const url = useJiraConfigurationUrl(configName)

    const items = [
        {
            key: 'configName',
            label: 'Jira server',
            children: <Space>
                <Tag>{configName}</Tag>
                {
                    url && <Link href={url}>{url}</Link>
                }
            </Space>,
            span: 12,
        },
        {
            key: 'sourceQuery',
            label: 'Source query',
            children: <Typography.Text code>{sourceQuery}</Typography.Text>,
            span: 12,
        },
        {
            key: 'targetQuery',
            label: 'Target query',
            children: <Typography.Text code>{targetQuery}</Typography.Text>,
            span: 12,
        },
        {
            key: 'linkName',
            label: 'Link name',
            children: <Tag>{linkName}</Tag>,
            span: 12,
        },
    ]

    return (
        <>
            <Descriptions
                items={items}
                column={12}
            />
        </>
    )
}