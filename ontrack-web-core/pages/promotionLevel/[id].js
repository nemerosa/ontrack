import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import PromotionLevelView from "@components/promotionLevels/PromotionLevelView";
import StoredChartOptionsCommandContextProvider from "@components/charts/ChartOptionsDialog";

export default function PromotionLevelPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <StoredChartOptionsCommandContextProvider id="promotion-charts">
                        <PromotionLevelView id={id}/>
                    </StoredChartOptionsCommandContextProvider>
                </MainLayout>
            </main>
        </>
    )
}