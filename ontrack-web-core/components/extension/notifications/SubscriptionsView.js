import Head from "next/head";
import {useEffect, useMemo, useState} from "react";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {List, Skeleton, Space, Tag, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {FaRegPaperPlane} from "react-icons/fa";
import SubscriptionContentTemplate from "@components/extension/notifications/SubscriptionContentTemplate";
import {pageTitle} from "@components/common/Titles";
import {CloseCommand} from "@components/common/Commands";

export default function SubscriptionsView({title, breadcrumbs = [], closeUri = '', additionalFilter = {}}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)

    const filter = useMemo(() => ({
        ...additionalFilter,
    }), [additionalFilter])

    const [items, setItems] = useState([])

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query Subscriptions(
                        $offset: Int!,
                        $size: Int!,
                        $filter: EventSubscriptionFilter!,
                    ) {
                        eventSubscriptions(
                            offset: $offset,
                            size: $size,
                            filter: $filter,
                        ) {
                            pageInfo {
                                nextPage {
                                    offset
                                    size
                                }
                            }
                            pageItems {
                                id
                                channel
                                channelConfig
                                channelConfigText
                                disabled
                                events
                                keywords
                                contentTemplate
                            }
                        }
                    }
                `,
                {
                    offset: 0,
                    size: 100,
                    filter,
                }
            ).then(data => {
                setItems(data.eventSubscriptions.pageItems)
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, filter]);

    const getActions = (item) => {
        const actions = []

        if (item.disabled) {
            actions.push(
                <Typography.Text type="secondary">Disabled</Typography.Text>
            )
        }

        // TODO Delete a subscription

        return actions
    }

    return (
        <>
            <Head>
                {pageTitle(title)}
            </Head>
            <MainPage
                title="Subscriptions"
                breadcrumbs={breadcrumbs}
                commands={[
                    <CloseCommand key="close" href={closeUri}/>,
                ]}
            >
                <Skeleton active loading={loading}>
                    <List
                        dataSource={items}
                        itemLayout="horizontal"
                        renderItem={(item) => (
                            <List.Item
                                actions={getActions(item)}
                            >
                                <List.Item.Meta
                                    title={
                                        <Space>
                                            <Tag color="processing">
                                                <Space>
                                                    <FaRegPaperPlane/>
                                                    {item.channel}
                                                </Space>
                                            </Tag>
                                            {
                                                item.channelConfigText &&
                                                <>
                                                    to
                                                    <Tag>
                                                        {item.channelConfigText}
                                                    </Tag>
                                                </>
                                            }
                                            <Typography.Text>on</Typography.Text>
                                            {
                                                item.events.map((event, index) => (
                                                    <Tag key={index} color="success">{event}</Tag>
                                                ))
                                            }
                                            {
                                                item.keywords &&
                                                <>
                                                    <Typography.Text>with keywords</Typography.Text>
                                                    <Tag>{item.keywords}</Tag>
                                                </>
                                            }
                                        </Space>
                                    }
                                    description={<SubscriptionContentTemplate template={item.contentTemplate}/>}
                                />
                            </List.Item>
                        )}
                    >

                    </List>
                </Skeleton>
            </MainPage>
        </>
    )
}