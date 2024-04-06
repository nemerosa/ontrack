import SubscriptionsView from "@components/extension/notifications/SubscriptionsView";
import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";
import {isAuthorized} from "@components/common/authorizations";

export default function SubscriptionsEntityView({type, id}) {

    const {title, breadcrumbs, closeUri, entity} = useProjectEntityPageInfo(type, id)

    return (
        <>
            <SubscriptionsView
                title={title}
                breadcrumbs={breadcrumbs}
                closeUri={closeUri}
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