import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import HookRecordsTable from "@components/extension/hook/HookRecordsTable";

export default function HookRecordsView() {
    return (
        <>
            <Head>
                {pageTitle("Hook records")}
            </Head>
            <MainPage
                title="Hook records"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <HookRecordsTable/>
            </MainPage>
        </>
    )
}