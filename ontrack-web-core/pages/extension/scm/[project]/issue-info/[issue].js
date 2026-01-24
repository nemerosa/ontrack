import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import SCMIssueInfoView from "@components/extension/scm/SCMIssueInfoView";

export default function GitIssueInfoPage() {
    const router = useRouter()
    const {project: projectName, issue} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <SCMIssueInfoView projectName={projectName} issueKey={issue}/>
                </MainLayout>
            </main>
        </>
    )
}