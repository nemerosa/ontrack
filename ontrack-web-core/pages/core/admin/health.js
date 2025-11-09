import MainLayout from "@components/layouts/MainLayout";
import SystemHealthView from "@components/core/admin/health/SystemHealthView";

export default function HealthPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <SystemHealthView/>
                </MainLayout>
            </main>
        </>
    )
}