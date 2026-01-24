import {useRouter} from "next/router";
import MainLayout from "@components/layouts/MainLayout";
import SCMCommitInfoView from "@components/extension/scm/SCMCommitInfoView";

export default function SCMCommitInfoPage() {
    const router = useRouter()
    const {project: projectName, commit} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <SCMCommitInfoView projectName={projectName} commit={commit}/>
                </MainLayout>
            </main>
        </>
    )
}