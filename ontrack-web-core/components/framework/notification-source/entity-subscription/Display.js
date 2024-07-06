import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";
import Link from "next/link";
import {Space} from "antd";
import SubscriptionLink from "@components/extension/notifications/SubscriptionLink";

export default function EntitySubscriptionNotificationSource({entityType, entityId, subscriptionName}) {

    const {title, uri, entity} = useProjectEntityPageInfo(entityType, entityId)

    return (
        <>
            <Space direction="vertical">
                <Link href={uri}>{title}</Link>
                <SubscriptionLink
                    subscription={{name: subscriptionName}}
                    entity={{
                        type: entityType,
                        id: entityId,
                    }}
                />
            </Space>
        </>

    )
}