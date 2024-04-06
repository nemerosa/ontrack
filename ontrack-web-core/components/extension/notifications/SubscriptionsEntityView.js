import SubscriptionsView from "@components/extension/notifications/SubscriptionsView";
import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";

export default function SubscriptionsEntityView({type, id}) {

    const {title, breadcrumbs, closeUri} = useProjectEntityPageInfo(type, id)

    return (
        <>
            <SubscriptionsView
                title={title}
                breadcrumbs={breadcrumbs}
                closeUri={closeUri}
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