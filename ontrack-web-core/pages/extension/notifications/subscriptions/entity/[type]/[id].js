import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import SubscriptionsEntityView from "@components/extension/notifications/SubscriptionsEntityView";

export default function SubscriptionsEntityPage() {
    const router = useRouter()
    const {type, id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <SubscriptionsEntityView type={type} id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}