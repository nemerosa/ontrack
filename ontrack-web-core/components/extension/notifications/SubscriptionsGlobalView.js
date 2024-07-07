import SubscriptionsView from "@components/extension/notifications/SubscriptionsView";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {homeUri} from "@components/common/Links";
import {UserContext} from "@components/providers/UserProvider";
import {useContext} from "react";

export default function SubscriptionsGlobalView() {

    const user = useContext(UserContext)

    return (
        <>
            <SubscriptionsView
                title="Global subscriptions"
                viewTitle="Global subscriptions"
                breadcrumbs={homeBreadcrumbs()}
                closeUri={homeUri()}
                managePermission={user.authorizations.subscriptions?.edit}
                additionalFilter={{}}
            />
        </>
    )

}