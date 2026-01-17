import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BranchLinksTableView from "@components/links/BranchLinksTableView";

export default function BranchLinksTablePage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BranchLinksTableView id={Number(id)}/>
                </MainLayout>
            </main>
        </>
    )
}