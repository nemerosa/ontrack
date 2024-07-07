import {Typography} from "antd";

export default function SubscriptionName({subscription, entity, managePermission}) {
    return (
        <>
            <Typography.Text
            >
                {subscription.name}
            </Typography.Text>
        </>
    )
}