import Head from "next/head";
import {useEffect, useMemo, useState} from "react";
import MainPage from "@components/layouts/MainPage";
import {List, Skeleton, Space, Tag, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {FaRegPaperPlane} from "react-icons/fa";
import SubscriptionContentTemplate from "@components/extension/notifications/SubscriptionContentTemplate";
import {pageTitle} from "@components/common/Titles";
import {CloseCommand} from "@components/common/Commands";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";

export default function SubscriptionsView({title, breadcrumbs = [], closeUri = '', managePermission = false, additionalFilter = {}}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)

    const filter = useMemo(() => ({
        ...additionalFilter,
    }), [additionalFilter])

    const [items, setItems] = useState([])
    const [refresh, setRefresh] = useState(0)

    const reload = () => {
        setRefresh(it => it + 1)
    }

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
    }, [client, filter, refresh]);

    const onDeleteSubscription = (item) => {
        return () => {
            client.request(
                gql`
                    mutation DeleteSubscription(
                        $id: String!,
                        $projectEntity: ProjectEntityIDInput,
                    ) {
                        deleteSubscription(input: {
                            id: $id,
                            projectEntity: $projectEntity,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                `,
                {
                    id: item.id,
                    projectEntity: additionalFilter.entity,
                }
            ).then(reload)
        }
    }

    const getActions = (item) => {
        const actions = []

        if (item.disabled) {
            actions.push(
                <Typography.Text type="secondary">Disabled</Typography.Text>
            )
        }

        // Delete a subscription
        if (managePermission) {
            actions.push(
                <InlineConfirmCommand
                    title="Deletes the subscription"
                    confirm="Do you really want to delete this subscription?"
                    onConfirm={onDeleteSubscription(item)}
                />
            )
        }

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