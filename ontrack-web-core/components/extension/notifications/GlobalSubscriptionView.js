import SubscriptionView from "@components/extension/notifications/SubscriptionView";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import SubscriptionsLink from "@components/extension/notifications/SubscriptionsLink";

export default function GlobalSubscriptionView({name}) {
    return (
        <>
            <SubscriptionView
                title={"Global subscription"}
                breadcrumbs={homeBreadcrumbs().concat(<SubscriptionsLink/>)}
                name={name}
            />
        </>
    )
}