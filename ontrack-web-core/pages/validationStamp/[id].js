import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import StoredChartOptionsCommandContextProvider from "@components/charts/ChartOptionsDialog";
import ValidationStampView from "@components/validationStamps/ValidationStampView";

export default function ValidationStampPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <StoredChartOptionsCommandContextProvider id="validation-charts">
                        <ValidationStampView id={Number(id)}/>
                    </StoredChartOptionsCommandContextProvider>
                </MainLayout>
            </main>
        </>
    )
}