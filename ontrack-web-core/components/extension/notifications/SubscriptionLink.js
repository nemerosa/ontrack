import Link from "next/link";
import {Typography} from "antd";
import SubscriptionName from "@components/extension/notifications/SubscriptionName";

export default function SubscriptionLink({subscription, entity, managePermission}) {
    if (entity) {
        return <Link
            href={`/extension/notifications/subscriptions/details?type=${entity.type}&id=${entity.id}&name=${subscription.name}`}>
            <SubscriptionName subscription={subscription} entity={entity} managePermission={managePermission}/>
        </Link>
    } else {
        return <Link
            href={`/extension/notifications/subscriptions/details?name=${subscription.name}`}>
            <SubscriptionName subscription={subscription} entity={entity} managePermission={managePermission}/>
        </Link>
    }
}