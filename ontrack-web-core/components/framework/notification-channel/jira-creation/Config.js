import {Descriptions, Space, Tag, Typography} from "antd";
import Link from "next/link";
import YesNo from "@components/common/YesNo";
import JiraCustomFields from "@components/extension/jira/JiraCustomFields";
import {useJiraConfigurationUrl} from "@components/extension/jira/Utils";

export default function JiraCreationNotificationChannelConfig({
                                                                  configName,
                                                                  projectName,
                                                                  issueType,
                                                                  labels,
                                                                  useExisting,
                                                                  fixVersion,
                                                                  assignee,
                                                                  titleTemplate,
                                                                  customFields,
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
            span: 6,
        },
        {
            key: 'projectName',
            label: 'Jira project',
            children: <Tag>{projectName}</Tag>,
            span: 6,
        },
        {
            key: 'issueType',
            label: 'Issue type',
            children: <Tag>{issueType}</Tag>,
            span: 6,
        },
        {
            key: 'labels',
            label: 'Labels',
            children: labels && labels.map(label => (<Tag key={label}>{label}</Tag>)),
            span: 12,
        },
        {
            key: 'useExisting',
            label: 'Using existing issue',
            children: <YesNo value={useExisting}/>,
            span: 6,
        },
        {
            key: 'fixVersion',
            label: 'Fix version',
            children: <Typography.Text code>{fixVersion}</Typography.Text>,
            span: 6,
        },
        {
            key: 'assignee',
            label: 'Assignee',
            children: <Typography.Text>{assignee}</Typography.Text>,
            span: 6,
        },
        {
            key: 'titleTemplate',
            label: 'Title',
            children: <Typography.Text code>{titleTemplate}</Typography.Text>,
            span: 12,
        },
        {
            key: 'customFields',
            label: 'Custom fields',
            children: <JiraCustomFields customFields={customFields}/>,
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