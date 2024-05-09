import MainLayout from "@components/layouts/MainLayout";
import WorkflowsAuditView from "@components/extension/workflows/WorkflowsAuditView";

export default function WorkflowsAudit() {
    return (
        <>
            <main>
                <MainLayout>
                    <WorkflowsAuditView/>
                </MainLayout>
            </main>
        </>
    )
}