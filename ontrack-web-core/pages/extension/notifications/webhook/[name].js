import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import WebhookExchangesView from "@components/extension/notifications/webhooks/WebhookExchangesView";

export default function WebhookPage() {
    const router = useRouter()
    const {name} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <WebhookExchangesView name={name}/>
                </MainLayout>
            </main>
        </>
    )
}