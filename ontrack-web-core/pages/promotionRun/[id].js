import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import PromotionRunView from "@components/promotionRuns/PromotionRunView";

export default function PromotionRunPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <PromotionRunView id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}