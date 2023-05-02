import Head from 'next/head'
import MainLayout from "@components/layouts/MainLayout";
import HomeView from "@components/views/HomeView";

export default function HomePage() {
    return (
        <>
            <Head>
                <title>Ontrack</title>
            </Head>
            <main>
                <MainLayout>
                    <HomeView/>
                </MainLayout>
            </main>
        </>
    )
}
