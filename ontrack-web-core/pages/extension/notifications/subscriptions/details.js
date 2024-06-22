import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import EntitySubscriptionView from "@components/extension/notifications/EntitySubscriptionView";
import GlobalSubscriptionView from "@components/extension/notifications/GlobalSubscriptionView";

export default function SubscriptionDetailsPage() {
    const router = useRouter()
    const {type, id, name} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    {
                        type && id &&
                        <EntitySubscriptionView type={type} id={id} name={name}/>
                    }
                    {
                        (!type || !id) &&
                        <GlobalSubscriptionView name={name}/>
                    }
                </MainLayout>
            </main>
        </>
    )
}