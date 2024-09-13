import Link from "next/link";
import SubscriptionName from "@components/extension/notifications/SubscriptionName";

export default function SubscriptionLink({subscription, text, entity, managePermission, onRenamed}) {
    if (entity) {
        return <Link
            href={`/extension/notifications/subscriptions/details?type=${entity.type}&id=${entity.id}&name=${subscription.name}`}>
            <SubscriptionName subscription={subscription} text={text} entity={entity} managePermission={managePermission}
                              onRenamed={onRenamed}/>
        </Link>
    } else {
        return <Link
            href={`/extension/notifications/subscriptions/details?name=${subscription.name}`}>
            <SubscriptionName subscription={subscription} text={text} entity={entity} managePermission={managePermission}
                              onRenamed={onRenamed}/>
        </Link>
    }
}