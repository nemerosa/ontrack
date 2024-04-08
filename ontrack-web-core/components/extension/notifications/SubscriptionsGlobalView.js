import SubscriptionsView from "@components/extension/notifications/SubscriptionsView";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {homeUri} from "@components/common/Links";

export default function SubscriptionsGlobalView() {
    return (
        <>
            <SubscriptionsView
                title="Global subscriptions"
                viewTitle="Global subscriptions"
                breadcrumbs={homeBreadcrumbs()}
                closeUri={homeUri()}
                managePermission={true} // TODO Checks global auths
                additionalFilter={{}}
            />
        </>
    )

}