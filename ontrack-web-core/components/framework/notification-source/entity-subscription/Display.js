import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";
import {Space, Tooltip, Typography} from "antd";
import SubscriptionLink from "@components/extension/notifications/SubscriptionLink";

export default function EntitySubscriptionNotificationSource({entityType, entityId, subscriptionName}) {

    const {title, entityTypeName} = useProjectEntityPageInfo(entityType, entityId)

    return (
        <>
            <Tooltip title={`Subscription for a ${entityTypeName}`}>
                <Space>
                    <SubscriptionLink
                        subscription={{
                            name: subscriptionName,
                        }}
                        text={
                            <>
                                <Typography.Link>
                                    {subscriptionName} @ {title}
                                </Typography.Link>
                            </>
                        }
                        entity={{
                            type: entityType,
                            id: entityId,
                        }}
                    />
                </Space>
            </Tooltip>
        </>

    )
}