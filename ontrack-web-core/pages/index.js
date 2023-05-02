import Head from 'next/head'
import MainLayout from "@components/layouts/MainLayout";

export default function HomePage() {
    return (
        <>
            <Head>
                <title>Ontrack</title>
            </Head>
            <main>
                <MainLayout>
                    Home page
                </MainLayout>
            </main>
        </>
    )
}
