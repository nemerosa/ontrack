import {Space} from "antd";
import SubscriptionLink from "@components/extension/notifications/SubscriptionLink";

export default function EntitySubscriptionNotificationSource({subscriptionName}) {
    return (
        <>
            <Space direction="vertical">
                <SubscriptionLink
                    subscription={{name: subscriptionName}}
                />
            </Space>
        </>

    )
}