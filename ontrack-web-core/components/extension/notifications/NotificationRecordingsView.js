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
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";

const {Column} = Table

export default function NotificationRecordingsView() {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [records, setRecords] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query NotificationRecords {
                        notificationRecords {
                            pageInfo {
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                key: id
                                channel
                                channelConfig
                                event
                                result {
                                    type
                                    message
                                    output
                                }
                                timestamp
                            }
                        }
                    }
                `
            ).then(data => {
                setRecords(data.notificationRecords.pageItems)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client]);

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
                        />

                        <Column
                            key="channelConfig"
                            title="Channel config"
                            render={(_, record) => (
                                <>
                                    <NotificationChannelConfig
                                        channel={record.channel}
                                        config={record.channelConfig}
                                    />
                                </>
                            )}
                        />

                        <Column
                            key="result"
                            title="Result"
                            render={(_, record) => <NotificationResultType type={record.result.type}/>}
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