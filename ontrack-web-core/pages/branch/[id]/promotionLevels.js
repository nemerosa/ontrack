import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BranchPromotionLevelsView from "@components/branches/promotionLevels/BranchPromotionLevelsView";

export default function BranchPromotionLevelsPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BranchPromotionLevelsView id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}