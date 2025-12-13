import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import {gql} from "graphql-request";
import {List, Tag, Typography} from "antd";
import SubscriptionLink from "@components/extension/notifications/SubscriptionLink";
import SubscriptionsLink from "@components/extension/notifications/SubscriptionsLink";

/**
 * This component displays a list of the subscriptions attached to a given entity.
 *
 * Each subscription can be navigated to and a general link allows to go the list
 * of all subscriptions for this entity.
 *
 * @param type Project entity type
 * @param id Project entity ID
 */
export default function EntitySubscriptions({type, id}) {

    const client = useGraphQLClient()
    const [subscriptions, setSubscriptions] = useState([])
    const [pageInfo, setPageInfo] = useState({})

    useEffect(() => {
        if (client) {
            client.request(
                gql`
                    query GetEntitySubscriptions($entity: ProjectEntityIDInput!) {
                        eventSubscriptions(size: 10, filter: {
                            entity: $entity,
                        }) {
                            pageInfo {
                                totalSize
                                nextPage {
                                    offset
                                }
                            }
                            pageItems {
                                name
                                channel
                            }
                        }
                    }
                `,
                {entity: {type, id: Number(id)}}
            ).then(data => {
                setSubscriptions(data.eventSubscriptions.pageItems)
                setPageInfo(data.eventSubscriptions.pageInfo)
            })
        }
    }, [client, type, id])

    return (
        <>
            <List
                dataSource={subscriptions}
                header={
                    <Typography.Title type="secondary" level={5}>Subscriptions (<SubscriptionsLink entity={{type, id}}
                                                                                                   text={pageInfo.totalSize}
                    />)</Typography.Title>
                }
                size="small"
                renderItem={(subscription) => (
                    <List.Item>
                        <List.Item.Meta
                            title={<SubscriptionLink entity={{type, id}} subscription={subscription}/>}
                            avatar={<Tag>{subscription.channel}</Tag>}
                        />
                    </List.Item>
                )}
                footer={
                    <div style={{paddingLeft: 16}}>
                        {
                            pageInfo.nextPage &&
                            <SubscriptionsLink entity={{type, id}}
                                               text="More..."
                            />
                        }
                    </div>
                }
            />
        </>
    )
}