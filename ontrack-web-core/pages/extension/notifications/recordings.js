import MainLayout from "@components/layouts/MainLayout";
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