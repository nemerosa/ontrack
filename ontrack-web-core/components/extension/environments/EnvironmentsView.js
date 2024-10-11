import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";

export default function EnvironmentsView() {
    return (
        <>
            <Head>
                {pageTitle("Environments")}
            </Head>
            <MainPage
                title="Environments"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>
                ]}
            >
            </MainPage>
        </>
    )
}