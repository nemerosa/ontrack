import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import {useEffect, useState} from "react";
import {Skeleton, Table} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import Timestamp from "@components/common/Timestamp";
import NotificationResultType from "@components/extension/notifications/NotificationResultType";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";
import TablePaginationFooter from "@components/common/table/TablePaginationFooter";
import TableColumnFilterDropdown from "@components/common/table/TableColumnFilterDropdown";
import SelectNotificationResultType from "@components/extension/notifications/SelectNotificationResultType";
import SelectNotificationChannel from "@components/extension/notifications/SelectNotificationChannel";
import NotificationSourceData from "@components/extension/notifications/NotificationSourceData";
import {gqlNotificationRecordContent} from "@components/extension/notifications/NotificationRecordsGraphQLFragments";

const {Column} = Table

export default function NotificationRecordingsView() {

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
                    ) {
                        notificationRecords(
                            offset: $offset,
                            size: $size,
                            channel: $channel,
                            resultType: $resultType,
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
            <Head>
                {pageTitle("Notification recordings")}
            </Head>
            <MainPage
                title="Notification recordings"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="home" href={homeUri()}/>
                ]}
            >
                <Skeleton active loading={loading}>
                    <Table
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
                            render={(_, record) => <Timestamp
                                value={record.timestamp}
                                format="YYYY MMM DD, HH:mm:ss"
                            />}
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

                    </Table>
                </Skeleton>
            </MainPage>
        </>
    )
}