import Head from "next/head";
import {useEffect, useMemo, useState} from "react";
import MainPage from "@components/layouts/MainPage";
import {List, Skeleton, Typography} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";
import {FaPlus} from "react-icons/fa";
import {pageTitle} from "@components/common/Titles";
import {CloseCommand, Command} from "@components/common/Commands";
import InlineConfirmCommand from "@components/common/InlineConfirmCommand";
import SubscriptionDialog, {useSubscriptionDialog} from "@components/extension/notifications/SubscriptionDialog";
import SubscriptionCard from "@components/extension/notifications/SubscriptionCard";
import {useDeleteSubscription} from "@components/extension/notifications/DeleteSubscription";

export default function SubscriptionsView({
                                              title,
                                              breadcrumbs = [],
                                              closeUri = '',
                                              managePermission = false,
                                              viewTitle = "Subscriptions",
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
                                name
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

    const {deleteSubscription} = useDeleteSubscription()

    const onDeleteSubscription = (item) => {
        return async () => {
            await deleteSubscription({
                name: item.name,
                entity: additionalFilter.entity,
            })
            reload()
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
                title={viewTitle}
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
                                <SubscriptionCard
                                    subscription={item}
                                    entity={additionalFilter.entity}
                                    actions={getActions(item)}
                                />
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