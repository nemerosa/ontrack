import Link from "next/link";

export const subscriptionsLink = (entity) => {
    if (entity) {
        return `/extension/notifications/subscriptions/entity/${entity.type}/${entity.id}`
    } else {
        return `/extension/notifications/subscriptions/global`
    }
}

export default function SubscriptionsLink({entity, text = 'Subscriptions'}) {
    return <Link title="List of all subscriptions" href={subscriptionsLink(entity)}>{text}</Link>
}