import {Descriptions, Space, Tag, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import Link from "next/link";
import {FaArrowRight} from "react-icons/fa";
import Duration from "@components/common/Duration";

export default function JiraCreationNotificationChannelConfig({
                                                                  configName,
                                                                  projectName,
                                                                  issueType,
                                                                  labels,
                                                                  fixVersion,
                                                                  assignee,
                                                                  titleTemplate,
                                                                  customFields,
                                                              }) {

    const client = useGraphQLClient()
    const [url, setUrl] = useState('')

    useEffect(() => {
        if (client && configName) {
            client.request(
                gql`
                    query GetJiraConfiguration($config: String!) {
                        jiraConfiguration(name: $config) {
                            url
                        }
                    }
                `,
                {config: configName}
            ).then(data => {
                setUrl(data.jiraConfiguration?.url)
            })
        }
    }, [client, configName]);

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
            children: labels.map(label => (<Tag key={label}>{label}</Tag>)),
            span: 12,
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
            children: <Descriptions
                column={12}
                items={
                    customFields.map(({name, value}) => ({
                        key: name,
                        label: name,
                        children: <Typography.Text code>{JSON.stringify(value, null, 2)}</Typography.Text>,
                        span: 12,
                    }))
                }
            />,
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