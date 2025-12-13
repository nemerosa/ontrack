import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import GitHubIngestionHookPayloadsTable from "@components/extension/github/ingestion/GitHubIngestionHookPayloadsTable";

export default function GitHubIngestionHookPayloadsView() {
    return (
        <>
            <Head>
                {pageTitle("GitHub Ingestion Hook Payloads")}
            </Head>
            <MainPage
                title="GitHub Ingestion Hook Payloads"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <GitHubIngestionHookPayloadsTable/>
            </MainPage>
        </>
    )
}