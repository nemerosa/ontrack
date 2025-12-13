import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import ScmChangeLogView from "@components/extension/scm/ScmChangeLogView";

export default function GitChangeLogPage() {
    const router = useRouter()
    const {from, to} = router.query
    return (
        <>
            <main>
                <MainLayout>
                    <ScmChangeLogView from={Number(from)} to={Number(to)}/>
                </MainLayout>
            </main>
        </>
    )
}