import SubscriptionView from "@components/extension/notifications/SubscriptionView";
import {useProjectEntityPageInfo} from "@components/entities/ProjectEntityPageInfo";

export default function EntitySubscriptionView({type, id, name}) {

    const {title, breadcrumbs, closeUri, entity} = useProjectEntityPageInfo(type, id)

    return (
        <>
            <SubscriptionView
                title={title}
                breadcrumbs={breadcrumbs}
                entity={{type, id}}
                name={name}
            />
        </>
    )
}