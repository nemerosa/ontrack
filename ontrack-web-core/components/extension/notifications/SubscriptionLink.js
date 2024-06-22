import Link from "next/link";

export default function SubscriptionLink({subscription, entity}) {
    if (entity) {
        return <Link href={`/extension/notifications/subscriptions/details?type=${entity.type}&id=${entity.id}&name=${subscription.name}`}>{subscription.name}</Link>
    } else {
        return <Link href={`/extension/notifications/subscriptions/details?name=${subscription.name}`}>{subscription.name}</Link>
    }
}