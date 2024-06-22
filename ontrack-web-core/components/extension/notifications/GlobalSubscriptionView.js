import SubscriptionView from "@components/extension/notifications/SubscriptionView";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";

export default function GlobalSubscriptionView({name}) {
    return (
        <>
            <SubscriptionView
                title={"Global subscription"}
                breadcrumbs={homeBreadcrumbs()}
                name={name}
            />
        </>
    )
}