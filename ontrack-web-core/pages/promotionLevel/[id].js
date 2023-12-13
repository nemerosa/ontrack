import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import PromotionLevelView from "@components/promotionLevels/PromotionLevelView";

export default function PromotionLevelPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <PromotionLevelView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}