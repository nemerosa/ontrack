import {Form, Tag} from "antd";
import {gql} from "graphql-request";
import NotificationResultType from "@components/extension/notifications/NotificationResultType";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";
import NotificationSourceData from "@components/extension/notifications/NotificationSourceData";
import {gqlNotificationRecordContent} from "@components/extension/notifications/NotificationRecordsGraphQLFragments";
import TimestampText from "@components/common/TimestampText";
import Link from "next/link";
import EventEntity from "@components/core/model/EventEntity";
import NotificationRecordResultLink from "@components/extension/notifications/NotificationRecordResultLink";
import StandardTable from "@components/common/table/StandardTable";
import SelectNotificationChannel from "@components/extension/notifications/SelectNotificationChannel";
import SelectNotificationResultType from "@components/extension/notifications/SelectNotificationResultType";

export default function NotificationRecordingsTable({entity, sourceId}) {
    return (
        <>
            <StandardTable
                filterForm={[
                    <Form.Item
                        key="channel"
                        name="channel"
                        label="Channel"
                    >
                        <SelectNotificationChannel
                            style={{
                                width: '15em',
                            }}
                            allowClear={true}
                        />
                    </Form.Item>,
                    <Form.Item
                        key="resultType"
                        name="resultType"
                        label="Result"
                    >
                        <SelectNotificationResultType/>
                    </Form.Item>,
                ]}
                query={
                    gql`
                            query NotificationRecords(
                                $offset: Int!,
                                $size: Int!,
                                $channel: String,
                                $resultType: NotificationResultType,
                                $eventEntityType: ProjectEntityType,
                                $eventEntityId: Int,
                                $sourceId: String,
                            ) {
                                notificationRecords(
                                    offset: $offset,
                                    size: $size,
                                    channel: $channel,
                                    resultType: $resultType,
                                    eventEntityType: $eventEntityType,
                                    eventEntityId: $eventEntityId,
                                    sourceId: $sourceId,
                                ) {
                                    pageInfo {
                                        nextPage {
                                            offset
                                            size
                                        }
                                    }
                                    pageItems {
                                        ...NotificationRecordContent
                                    }
                                }
                            }
                            ${gqlNotificationRecordContent}
                    `
                }
                variables={{
                    eventEntityType: entity?.type,
                    eventEntityId: entity?.id,
                    sourceId: sourceId,
                }}
                queryNode="notificationRecords"
                filter={{}}
                expandable={{
                    expandedRowRender: (record) => (
                        <>
                            <NotificationRecordDetails record={record}/>
                        </>
                    )
                }}
                rowKey={record => record.key}
                columns={[
                    {
                        key: "timestamp",
                        title: "Timestamp",
                        render: (_, record) => <Link href={`/extension/notifications/recordings/${record.id}`}>
                            <TimestampText
                                value={record.timestamp}
                                format="YYYY MMM DD, HH:mm:ss"
                            />
                        </Link>
                    },
                    {
                        key: "channel",
                        title: "Channel",
                        render: (_, record) => <Tag>{record.channel}</Tag>,
                    },
                    {
                        key: "link",
                        title: "Link",
                        render: (_, record) => <NotificationRecordResultLink
                            channel={record.channel}
                            result={record.result}
                        />,
                    },
                    {
                        key: "source",
                        title: "Source",
                        render: (_, record) => <NotificationSourceData source={record.source}/>,
                    },
                    {
                        key: "result",
                        title: "Result",
                        render: (_, record) => <NotificationResultType type={record.result.type}/>,
                    },
                    {
                        key: "event",
                        title: "Event",
                        render: (_, record) => <Tag>{record.event.eventType.id}</Tag>,
                    },
                    {
                        key: "entity",
                        title: "Entity",
                        render: (_, record) => <EventEntity event={record.event}/>,
                    },
                ]}
            />
        </>
    )
}