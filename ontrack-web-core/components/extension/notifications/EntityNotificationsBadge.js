import {useGraphQLClient} from "@components/providers/ConnectionContextProvider";
import {useEffect, useState} from "react";
import LoadingInline from "@components/common/LoadingInline";
import {gql} from "graphql-request";
import NotificationStatusBadge from "@components/extension/notifications/NotificationStatusBadge";
import {Badge, Space} from "antd";

export default function EntityNotificationsBadge({entityType, entityId, href, showText = false, children}) {

    const client = useGraphQLClient()
    const [loading, setLoading] = useState(true)
    const [statuses, setStatuses] = useState({})

    const [badgeCount, setBadgeCount] = useState(0)
    const [badgeColour, setBadgeColour] = useState('')

    useEffect(() => {
        if (client) {
            setLoading(true)
            client.request(
                gql`
                    query EntityNotificationsStatuses(
                        $entityType: ProjectEntityType!,
                        $entityId: Int!,
                    ) {
                        notificationRecords(
                            eventEntityType: $entityType,
                            eventEntityId: $entityId,
                            sourceId: "entity-subscription",
                        ) {
                            pageItems {
                                result {
                                    type
                                }
                            }
                        }
                    }
                `,
                {entityType, entityId: Number(entityId)}
            ).then(data => {
                const types = data.notificationRecords?.pageItems?.map(record => record.result.type) ?? []
                let success = 0
                let running = 0
                let error = 0
                types.forEach(type => {
                    switch (type) {
                        case 'OK':
                            success++
                            break
                        case 'ONGOING':
                        case 'ASYNC':
                            running++
                            break
                        default:
                            error++
                            break
                    }
                })
                setStatuses({success, running, error})
                if (error) {
                    setBadgeCount(error)
                    setBadgeColour("red")
                } else if (running) {
                    setBadgeCount(running)
                    setBadgeColour("blue")
                } else if (success) {
                    setBadgeCount(success)
                    setBadgeColour("green")
                }
            }).finally(() => {
                setLoading(false)
            })
        }
    }, [client, entityType, entityId])

    return (
        <>
            {
                children && <>
                    <Badge overflowCount={10} showZero={false} count={badgeCount} title="" color={badgeColour} size="small">
                        {children}
                    </Badge>
                </>
            }
            {
                !children &&
                <LoadingInline
                    loading={loading}
                    text=""
                >
                    <Space size={1}>
                        <NotificationStatusBadge
                            status="success"
                            count={statuses.success}
                            title={`${statuses.success} notification(s) have succeeded.`}
                            href={href}
                            showText={showText}
                        />
                        <NotificationStatusBadge
                            status="processing"
                            spin={true}
                            count={statuses.running}
                            title={`${statuses.running} notification(s) are still running.`}
                            href={href}
                            showText={showText}
                        />
                        <NotificationStatusBadge
                            status="error"
                            count={statuses.error}
                            title={`${statuses.error} notification(s) have failed.`}
                            href={href}
                            showText={showText}
                        />
                    </Space>
                </LoadingInline>
            }
        </>
    )
}