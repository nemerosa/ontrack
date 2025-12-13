import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import AutoVersioningConfigView from "@components/extension/auto-versioning/AutoVersioningConfigView";

export default function BranchAutoVersioningPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <AutoVersioningConfigView branchId={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}