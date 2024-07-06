import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import SubscriptionCard from "@components/extension/notifications/SubscriptionCard";
import {useEffect, useState} from "react";
import {Skeleton, Space, Table, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import NotificationRecordDetails from "@components/extension/notifications/NotificationRecordDetails";
import Timestamp from "@components/common/Timestamp";
import NotificationResultType from "@components/extension/notifications/NotificationResultType";
import {CloseCommand} from "@components/common/Commands";
import {subscriptionsLink} from "@components/extension/notifications/SubscriptionsLink";

const {Column} = Table

export default function SubscriptionView({title, breadcrumbs, entity, name}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [subscription, setSubscription] = useState()
    const [recordings, setRecordings] = useState([])

    useEffect(() => {
        if (client) {

            const loadSubscription = async () => {
                setLoading(true)
                try {
                    const data = await client.request(
                        gql`
                            query GetEntitySubscription(
                                $entity: ProjectEntityIDInput,
                                $name: String!,
                            ) {
                                eventSubscriptions(size: 1, filter: {entity: $entity, name: $name}) {
                                    pageItems {
                                        name
                                        channel
                                        channelConfig
                                        keywords
                                        events
                                        disabled
                                        contentTemplate
                                    }
                                }
                            }
                        `,
                        {
                            entity,
                            name,
                        }
                    )
                    setSubscription(data.eventSubscriptions.pageItems[0])

                    const dataRecordings = await client.request(
                        gql`
                            query GetSubscriptionRecordings(
                                $sourceId: String,
                                $sourceData: JSON,
                            ) {
                                notificationRecords(sourceId: $sourceId, sourceData: $sourceData) {
                                    pageItems {
                                        key: id
                                        source {
                                            id
                                            data
                                        }
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
                        `,
                        {
                            sourceId: entity ? 'entity-subscription' : 'global-subscription',
                            sourceData: entity ? {
                                entityType: entity.type,
                                entityId: Number(entity.id),
                                subscriptionName: name,
                            } : {
                                subscriptionName: name,
                            }
                        }
                    )
                    setRecordings(dataRecordings.notificationRecords.pageItems)
                } finally {
                    setLoading(false)
                }
            }

            // noinspection JSIgnoredPromiseFromCall
            loadSubscription()
        }
    }, [client, entity, name])

    return (
        <>
            <Head>
                {pageTitle(title)}
            </Head>
            <MainPage
                title={`Subscription: ${name}`}
                breadcrumbs={breadcrumbs}
                commands={[
                    <CloseCommand key="close" href={subscriptionsLink(entity)}/>
                ]}
            >
                <Skeleton active loading={loading}>
                    <Space direction="vertical" className="ot-line">

                        <SubscriptionCard
                            entity={entity}
                            actions={[]}
                            subscription={subscription}
                        />

                        <Typography.Title level={5} type="secondary">Recordings</Typography.Title>

                        <Table
                            dataSource={recordings}
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
                                key="result"
                                title="Result"
                                render={(_, record) => <NotificationResultType type={record.result.type}/>}
                            />

                        </Table>
                    </Space>
                </Skeleton>
            </MainPage>
        </>
    )
}