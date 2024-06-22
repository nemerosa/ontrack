import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import SubscriptionCard from "@components/extension/notifications/SubscriptionCard";
import {useEffect, useState} from "react";
import {Skeleton} from "antd";
import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {gql} from "graphql-request";

export default function SubscriptionView({title, breadcrumbs, entity, name}) {

    const client = useGraphQLClient()

    const [loading, setLoading] = useState(true)
    const [subscription, setSubscription] = useState()

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
                commands={[]}
            >
                <Skeleton active loading={loading}>
                    <SubscriptionCard
                        entity={entity}
                        actions={[]}
                        subscription={subscription}
                    />
                </Skeleton>
            </MainPage>
        </>
    )
}