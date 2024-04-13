import MainLayout from "@components/layouts/MainLayout";
import SubscriptionsGlobalView from "@components/extension/notifications/SubscriptionsGlobalView";
import NotificationRecordingsView from "@components/extension/notifications/NotificationRecordingsView";

export default function NotificationRecordingsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <NotificationRecordingsView/>
                </MainLayout>
            </main>
        </>
    )
}