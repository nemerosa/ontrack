import Head from "next/head";
import {useEffect, useMemo, useState} from "react";
import MainPage from "@components/layouts/MainPage";
import {Card, List, Skeleton, Space, Tag, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {FaPlus, FaRegPaperPlane} from "react-icons/fa";
import SubscriptionContentTemplate from "@components/extension/notifications/SubscriptionContentTemplate";
import {pageTitle} from "@components/common/Titles";
import {CloseCommand, Command} from "@components/common/Commands";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import SubscriptionDialog, {useSubscriptionDialog} from "@components/extension/notifications/SubscriptionDialog";
import EventList from "@components/core/model/EventList";
import NotificationChannelConfig from "@components/extension/notifications/NotificationChannelConfig";

export default function SubscriptionsView({
                                              title,
                                              breadcrumbs = [],
                                              closeUri = '',
                                              managePermission = false,
                                              additionalFilter = {}
                                          }) {

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

    const subscriptionDialog = useSubscriptionDialog({
        onSuccess: reload,
        projectEntity: additionalFilter.entity,
    })

    const onCreateSubscription = () => {
        subscriptionDialog.start()
    }

    const [commands, setCommands] = useState([])

    useEffect(() => {
        const commands = []

        if (managePermission) {
            commands.push(
                <Command key="create" icon={<FaPlus/>} action={onCreateSubscription} text="Create subscription"/>
            )
        }

        commands.push(<CloseCommand key="close" href={closeUri}/>)

        setCommands(commands)
    }, [managePermission, closeUri]);

    return (
        <>
            <Head>
                {pageTitle(title)}
            </Head>
            <MainPage
                title="Subscriptions"
                breadcrumbs={breadcrumbs}
                commands={commands}
            >
                <Skeleton active loading={loading}>
                    <List
                        grid={{
                            gutter: 16,
                            column: 2,
                        }}
                        dataSource={items}
                        itemLayout="horizontal"
                        renderItem={(item) => (
                            <List.Item>
                                <Card
                                    title={<Tag color="blue">{item.channel}</Tag>}
                                    extra={getActions(item)}
                                >
                                    <Card.Grid style={{width: '100%'}} hoverable={false}>
                                        <EventList events={item.events}/>
                                    </Card.Grid>
                                    {
                                        item.keywords &&
                                        <Card.Grid style={{width: '100%'}} hoverable={false}>
                                            Keywords: <Tag>{item.keywords}</Tag>
                                        </Card.Grid>
                                    }
                                    <Card.Grid style={{width: '100%'}} hoverable={false}>
                                        <NotificationChannelConfig channel={item.channel} config={item.channelConfig}/>
                                    </Card.Grid>
                                    {
                                        item.contentTemplate &&
                                        <Card.Grid style={{width: '100%'}} hoverable={false}>
                                            <p>Custom template:</p>
                                            <SubscriptionContentTemplate template={item.contentTemplate}/>
                                        </Card.Grid>
                                    }
                                </Card>
                            </List.Item>
                        )}
                    >

                    </List>
                </Skeleton>
            </MainPage>
            <SubscriptionDialog subscriptionDialog={subscriptionDialog}/>
        </>
    )
}