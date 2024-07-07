import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import EntitySubscriptionView from "@components/extension/notifications/EntitySubscriptionView";
import GlobalSubscriptionView from "@components/extension/notifications/GlobalSubscriptionView";

export default function SubscriptionDetailsPage() {
    const router = useRouter()
    const {type, id, name} = router.query

    const onRenamed = async (value) => {
        let url = `/extension/notifications/subscriptions/details?name=${value}`
        if (type && id) {
            url += `&type=${type}&id=${id}`
        }
        await router.push(url)
    }

    return (
        <>
            <main>
                <MainLayout>
                    {
                        type && id &&
                        <EntitySubscriptionView
                            key={router.asPath}
                            type={type}
                            id={id}
                            name={name}
                            onRenamed={onRenamed}
                        />
                    }
                    {
                        (!type || !id) &&
                        <GlobalSubscriptionView
                            key={router.asPath}
                            name={name}
                            onRenamed={onRenamed}
                        />
                    }
                </MainLayout>
            </main>
        </>
    )
}