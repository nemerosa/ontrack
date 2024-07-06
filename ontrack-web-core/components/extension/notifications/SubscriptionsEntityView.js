import SubscriptionsView from "@components/extension/notifications/SubscriptionsView";
import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";
import {isAuthorized} from "@components/common/authorizations";

export default function SubscriptionsEntityView({type, id}) {

    const {title, breadcrumbs, uri, entity} = useProjectEntityPageInfo(type, id, 'Subscriptions')

    return (
        <>
            <SubscriptionsView
                title={title}
                breadcrumbs={breadcrumbs}
                closeUri={uri}
                managePermission={isAuthorized(entity, 'subscriptions', 'edit')}
                additionalFilter={{
                    entity: {
                        type,
                        id
                    }
                }}
            />
        </>
    )
}