import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import GitCommitInfoView from "@components/extension/git/GitCommitInfoView";

export default function GitCommitInfoPage() {
    const router = useRouter()
    const {projectId, commit} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <GitCommitInfoView projectId={projectId} commit={commit}/>
                </MainLayout>
            </main>
        </>
    )
}