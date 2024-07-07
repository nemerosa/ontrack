import SubscriptionView from "@components/extension/notifications/SubscriptionView";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import SubscriptionsLink from "@components/extension/notifications/SubscriptionsLink";
import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";

export default function GlobalSubscriptionView({name, onRenamed}) {

    const user = useContext(UserContext)

    return (
        <>
            <SubscriptionView
                title={"Global subscription"}
                breadcrumbs={homeBreadcrumbs().concat(<SubscriptionsLink/>)}
                name={name}
                managePermission={user.authorizations.subscriptions?.edit}
                onRenamed={onRenamed}
            />
        </>
    )
}