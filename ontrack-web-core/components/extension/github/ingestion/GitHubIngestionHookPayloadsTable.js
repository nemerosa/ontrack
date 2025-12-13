import StandardTable from "@components/common/table/StandardTable";
import {gql} from "graphql-request";
import TimestampText from "@components/common/TimestampText";
import Link from "next/link";
import GitHubIngestionHookPayloadDetails
    from "@components/extension/github/ingestion/GitHubIngestionHookPayloadDetails";
import {Form, Input, Typography} from "antd";
import SelectIngestionHookPayloadStatus from "@components/extension/github/ingestion/SelectIngestionHookPayloadStatus";
import SelectIngestionEventProcessingResult
    from "@components/extension/github/ingestion/SelectIngestionEventProcessingResult";

export default function GitHubIngestionHookPayloadsTable() {
    return (
        <>
            <StandardTable
                tableSize="small"
                query={
                    gql`
                        query GitHubIngestionHookPayloads(
                            $uuid: String,
                            $owner: String,
                            $repository: String,
                            $gitHubDelivery: String,
                            $gitHubEvent: String,
                            $source: String,
                            $statuses: [IngestionHookPayloadStatus!],
                            $outcome: IngestionEventProcessingResult,
                        ) {
                            gitHubIngestionHookPayloads(
                                uuid: $uuid,
                                owner: $owner,
                                repository: $repository,
                                gitHubDelivery: $gitHubDelivery,
                                gitHubEvent: $gitHubEvent,
                                source: $source,
                                statuses: $statuses,
                                outcome: $outcome,
                            ) {
                                pageInfo {
                                    nextPage {
                                        offset
                                        size
                                    }
                                }
                                pageItems {
                                    uuid
                                    timestamp
                                    gitHubDelivery
                                    gitHubEvent
                                    source
                                    repository {
                                        owner {
                                            login
                                        }
                                        name
                                        htmlUrl
                                    }
                                    status
                                    outcome
                                    outcomeDetails
                                    payload
                                    completion
                                }
                            }
                        }
                    `
                }
                queryNode="gitHubIngestionHookPayloads"
                variables={{}}
                filter={{}}
                columns={[
                    {
                        title: "UUID",
                        dataIndex: "uuid",
                        render: (value) => <Typography.Text code copyable>{value}</Typography.Text>,
                    },
                    {
                        title: "Owner",
                        render: (_, record) => record.repository.owner.login,
                    },
                    {
                        title: "Repository",
                        render: (_, record) => <Link href={record.repository.htmlUrl}>{record.repository.name}</Link>,
                    },
                    {
                        title: "Timestamp",
                        dataIndex: "timestamp",
                        render: (value) => <TimestampText value={value} format="YYYY MMM DD, HH:mm:ss"/>,
                    },
                    {
                        title: "GitHub Delivery ID",
                        dataIndex: "gitHubDelivery",
                        render: (value) => <Typography.Text code copyable>{value}</Typography.Text>,
                    },
                    {
                        title: "Event",
                        dataIndex: "gitHubEvent",
                    },
                    {
                        title: "Source",
                        dataIndex: "source",
                    },
                    {
                        title: "Status",
                        dataIndex: "status",
                    },
                    {
                        title: "Outcome",
                        dataIndex: "outcome",
                    },
                    {
                        title: "Completion",
                        dataIndex: "completion",
                        render: (value) => <TimestampText value={value} format="YYYY MMM DD, HH:mm:ss"/>,
                    },
                ]}
                rowKey={record => record.uuid}
                expandable={{
                    expandedRowRender: (record) => <GitHubIngestionHookPayloadDetails record={record}/>
                }}
                filterForm={[
                    <Form.Item
                        key="uuid"
                        name="uuid"
                        label="UUID"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="owner"
                        name="owner"
                        label="Owner"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="repository"
                        name="repository"
                        label="Repository"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="gitHubDelivery"
                        name="gitHubDelivery"
                        label="Delivery"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="gitHubEvent"
                        name="gitHubEvent"
                        label="Event"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="source"
                        name="source"
                        label="Source"
                    >
                        <Input/>
                    </Form.Item>,
                    <Form.Item
                        key="status"
                        name="statuses"
                        label="Statuses"
                    >
                        <SelectIngestionHookPayloadStatus/>
                    </Form.Item>,
                    <Form.Item
                        key="outcome"
                        name="outcome"
                        label="Outcome"
                    >
                        <SelectIngestionEventProcessingResult/>
                    </Form.Item>,
                ]}
                autoRefresh={true}
            />
        </>
    )
}