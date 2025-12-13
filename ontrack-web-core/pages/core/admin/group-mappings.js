import MainLayout from "@components/layouts/MainLayout";
import GroupMappingsView from "@components/core/admin/account-management/GroupMappingsView";

export default function GroupMappingsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <GroupMappingsView/>
                </MainLayout>
            </main>
        </>
    )
}