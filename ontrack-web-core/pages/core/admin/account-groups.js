import MainLayout from "@components/layouts/MainLayout";
import AccountGroupsView from "@components/core/admin/account-management/AccountGroupsView";

export default function AccountGroupsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <AccountGroupsView/>
                </MainLayout>
            </main>
        </>
    )
}
