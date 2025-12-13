import MainLayout from "@components/layouts/MainLayout";
import HookRecordsView from "@components/extension/hook/HookRecordsView";

export default function HookRecordsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <HookRecordsView/>
                </MainLayout>
            </main>
        </>
    )
}