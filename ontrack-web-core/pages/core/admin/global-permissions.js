import MainLayout from "@components/layouts/MainLayout";
import GlobalPermissionsView from "@components/core/admin/account-management/GlobalPermissionsView";

export default function GlobalPermissionsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <GlobalPermissionsView/>
                </MainLayout>
            </main>
        </>
    )
}