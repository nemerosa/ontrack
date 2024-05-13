import MainLayout from "@components/layouts/MainLayout";
import WorkflowsAuditView from "@components/extension/workflows/WorkflowsAuditView";
import {useRouter} from "next/router";
import WorkflowInstanceView from "@components/extension/workflows/WorkflowInstanceView";

export default function WorkflowInstancePage() {
    const router = useRouter()
    const {id} = router.query

    return (
        <>
            <main>
                <MainLayout>
                    <WorkflowInstanceView id={id}/>
                </MainLayout>
            </main>
        </>
    )
}