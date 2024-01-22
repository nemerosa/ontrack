import Head from "next/head";
import {title} from "@components/common/Titles";
import MainLayout from "@components/layouts/MainLayout";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseToHomeCommand} from "@components/common/Commands";

export default function StandardPage({
                                         pageTitle,
                                         breadcrumbs = homeBreadcrumbs(),
                                         commands = [
                                             <CloseToHomeCommand key="home"/>,
                                         ],
                                         additionalCommands = [],
                                         children,
                                     }) {
    return (
        <>
            <main>
                <Head>
                    {title(pageTitle)}
                </Head>
                <MainLayout>
                    <MainPage
                        title={pageTitle}
                        breadcrumbs={breadcrumbs}
                        commands={[...additionalCommands, ...commands]}
                    >
                        {children}
                    </MainPage>
                </MainLayout>
            </main>

        </>
    )
}