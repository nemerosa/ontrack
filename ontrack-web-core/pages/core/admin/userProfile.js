import MainLayout from "@components/layouts/MainLayout";
import UserProfileView from "@components/core/admin/userProfile/UserProfileView";

export default function UserProfilePage() {
    return (
        <>
            <main>
                <MainLayout>
                    <UserProfileView/>
                </MainLayout>
            </main>
        </>
    )
}