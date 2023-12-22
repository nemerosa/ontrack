import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import GitChangeLogView from "@components/extension/git/GitChangeLogView";

export default function GitChangeLogPage() {
    const router = useRouter()
    const {from, to} = router.query
    return (
        <>
            <main>
                <MainLayout>
                    <GitChangeLogView from={from} to={to}/>
                </MainLayout>
            </main>
        </>
    )
}