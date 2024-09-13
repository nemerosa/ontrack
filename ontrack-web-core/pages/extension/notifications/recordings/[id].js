import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";

export default function NotificationRecordingPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    {id}
                </MainLayout>
            </main>
        </>
    )
}