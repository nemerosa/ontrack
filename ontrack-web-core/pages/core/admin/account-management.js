import MainLayout from "@components/layouts/MainLayout";
import AccountManagementView from "@components/core/admin/account-management/AccountManagementView";

export default function AccountManagementPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <AccountManagementView/>
                </MainLayout>
            </main>
        </>
    )
}
