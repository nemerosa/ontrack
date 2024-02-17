import MainLayout from "@components/layouts/MainLayout";
import HomeView from "@components/views/HomeView";

export default function HomePage() {
    return (
        <>
            <main>
                <MainLayout>
                    <HomeView/>
                </MainLayout>
            </main>
        </>
    )
}
