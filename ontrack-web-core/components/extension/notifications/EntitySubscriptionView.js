import SubscriptionView from "@components/extension/notifications/SubscriptionView";
import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";
import SubscriptionsLink from "@components/extension/notifications/SubscriptionsLink";
import {isAuthorized} from "@components/common/authorizations";

export default function EntitySubscriptionView({type, id, name}) {

    const {title, breadcrumbs, entity} = useProjectEntityPageInfo(type, id, 'Subscriptions')

    return (
        <>
            <SubscriptionView
                title={title}
                breadcrumbs={breadcrumbs.concat(<SubscriptionsLink entity={{type, id}}/>)}
                entity={{type, id}}
                managePermission={isAuthorized(entity, 'subscriptions', 'edit')}
                name={name}
            />
        </>
    )
}