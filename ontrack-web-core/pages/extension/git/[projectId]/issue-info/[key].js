import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import GitIssueInfoView from "@components/extension/git/GitIssueInfoView";

export default function GitIssueInfoPage() {
    const router = useRouter()
    const {projectId, key} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <GitIssueInfoView projectId={projectId} issueKey={key}/>
                </MainLayout>
            </main>
        </>
    )
}