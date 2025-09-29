import MainLayout from "@components/layouts/MainLayout";
import WebhooksView from "@components/extension/notifications/webhooks/WebhooksView";

export default function WebhooksPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <WebhooksView/>
                </MainLayout>
            </main>
        </>
    )
}