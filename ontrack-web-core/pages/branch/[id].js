import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import BranchView from "@components/views/BranchView";

export default function BranchPage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <BranchView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}