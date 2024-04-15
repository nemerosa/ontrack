import MainLayout from "@components/layouts/MainLayout";
import JobsView from "@components/core/admin/jobs/JobsView";

export default function JobsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <JobsView/>
                </MainLayout>
            </main>
        </>
    )
}