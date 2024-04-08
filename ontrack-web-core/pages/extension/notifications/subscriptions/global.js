import MainLayout from "@components/layouts/MainLayout";
import SubscriptionsGlobalView from "@components/extension/notifications/SubscriptionsGlobalView";

export default function SubscriptionsGlobalPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <SubscriptionsGlobalView/>
                </MainLayout>
            </main>
        </>
    )
}