import MainLayout from "@components/layouts/MainLayout";
import SettingsView from "@components/core/admin/settings/SettingsView";

export default function SettingsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <SettingsView/>
                </MainLayout>
            </main>
        </>
    )
}