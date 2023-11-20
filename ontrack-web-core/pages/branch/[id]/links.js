import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BranchLinksView from "@components/links/BranchLinksView";

export default function BranchLinksPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BranchLinksView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}