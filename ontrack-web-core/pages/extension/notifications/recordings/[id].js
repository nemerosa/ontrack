import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import NotificationRecordingView from "@components/extension/notifications/NotificationRecordingView";

export default function NotificationRecordingPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <NotificationRecordingView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}