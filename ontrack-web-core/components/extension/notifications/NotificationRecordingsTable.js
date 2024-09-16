import {useEffect, useState} from "react";
import {Table} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import NotificationResultType from "@components/extension/notifications/NotificationResultType";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";
import TablePaginationFooter from "@components/common/table/TablePaginationFooter";
import TableColumnFilterDropdown from "@components/common/table/TableColumnFilterDropdown";
import SelectNotificationResultType from "@components/extension/notifications/SelectNotificationResultType";
import SelectNotificationChannel from "@components/extension/notifications/SelectNotificationChannel";
import NotificationSourceData from "@components/extension/notifications/NotificationSourceData";
import {gqlNotificationRecordContent} from "@components/extension/notifications/NotificationRecordsGraphQLFragments";
import TimestampText from "@components/common/TimestampText";
import Link from "next/link";
import EventEntity from "@components/core/model/EventEntity";
import NotificationRecordResultLink from "@components/extension/notifications/NotificationRecordResultLink";

const {Column} = Table

export default function NotificationRecordingsTable({entity}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [records, setRecords] = useState([])

    const [pagination, setPagination] = useState({
        offset: 0,
        size: 10,
    })

    const [pageInfo, setPageInfo] = useState({})

    const [filter, setFilter] = useState({
        channel: null,
        resultType: null,
    })

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query NotificationRecords(
                        $offset: Int!,
                        $size: Int!,
                        $channel: String,
                        $resultType: NotificationResultType,
                        $eventEntityType: ProjectEntityType,
                        $eventEntityId: Int,
                    ) {
                        notificationRecords(
                            offset: $offset,
                            size: $size,
                            channel: $channel,
                            resultType: $resultType,
                            eventEntityType: $eventEntityType,
                            eventEntityId: $eventEntityId,
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
                `,
                {
                    offset: pagination.offset,
                    size: pagination.size,
                    eventEntityType: entity?.type,
                    eventEntityId: entity?.id,
                    ...filter,
                }
            ).then(data => {
                setPageInfo(data.notificationRecords.pageInfo)
                if (pagination.offset > 0) {
                    setRecords((entries) => [...entries, ...data.notificationRecords.pageItems])
                } else {
                    setRecords(data.notificationRecords.pageItems)
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, pagination, filter]);

    const onTableChange = (_, filters) => {
        setFilter({
            channel: filters.channel && filters.channel[0],
            resultType: filters.result && filters.result[0],
        })
    }

    return (
        <>
            <Table
                loading={loading}
                dataSource={records}
                pagination={false}
                expandable={{
                    expandedRowRender: (record) => (
                        <>
                            <NotificationRecordDetails record={record}/>
                        </>
                    )
                }}
                footer={() =>
                    <TablePaginationFooter
                        pageInfo={pageInfo}
                        setPagination={setPagination}
                    />
                }
                onChange={onTableChange}
            >

                <Column
                    key="timestamp"
                    title="Timestamp"
                    render={(_, record) => <Link href={`/extension/notifications/recordings/${record.id}`}>
                        <TimestampText
                            value={record.timestamp}
                            format="YYYY MMM DD, HH:mm:ss"
                        />
                    </Link>
                    }
                />

                <Column
                    key="channel"
                    title="Channel"
                    render={(_, record) => record.channel}
                    filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                        <TableColumnFilterDropdown
                            confirm={confirm}
                            clearFilters={clearFilters}
                        >
                            <SelectNotificationChannel
                                value={selectedKeys}
                                onChange={value => setSelectedKeys([value])}
                                style={{
                                    width: '15em',
                                }}
                                allowClear={true}
                            />
                        </TableColumnFilterDropdown>
                    }
                    filteredValue={filter.channel}
                />

                <Column
                    key="link"
                    title="Link"
                    render={(_, record) => (
                        <>
                            <NotificationRecordResultLink channel={record.channel} result={record.result}/>
                        </>
                    )}
                />

                <Column
                    key="source"
                    title="Source"
                    render={(_, record) => (
                        <>
                            <NotificationSourceData source={record.source}/>
                        </>
                    )}
                />

                <Column
                    key="result"
                    title="Result"
                    render={(_, record) => <NotificationResultType type={record.result.type}/>}
                    filterDropdown={({setSelectedKeys, selectedKeys, confirm, clearFilters}) =>
                        <TableColumnFilterDropdown
                            confirm={confirm}
                            clearFilters={clearFilters}
                        >
                            <SelectNotificationResultType
                                value={selectedKeys}
                                onChange={value => setSelectedKeys([value])}
                            />
                        </TableColumnFilterDropdown>
                    }
                    filteredValue={filter.resultType}
                />

                <Column
                    key="event"
                    title="Event"
                    render={(_, record) => record.event.eventType.id}
                />

                <Column
                    key="entity"
                    title="Entity"
                    render={(_, record) => (
                        <>
                            <EventEntity event={record.event}/>
                        </>
                    )}
                />

            </Table>
        </>
    )
}