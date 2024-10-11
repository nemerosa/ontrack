import MainLayout from "@components/layouts/MainLayout";
import EnvironmentsView from "@components/extension/environments/EnvironmentsView";

export default function EnvironmentsPage() {
    return (
        <>
            <main>
                <MainLayout>
                    <EnvironmentsView/>
                </MainLayout>
            </main>
        </>
    )
}