import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BranchPageView from "@components/branches/BranchPageView";

export default function BranchPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BranchPageView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}