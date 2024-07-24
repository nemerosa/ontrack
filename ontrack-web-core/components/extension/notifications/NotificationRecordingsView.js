import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import NotificationRecordingsTable from "@components/extension/notifications/NotificationRecordingsTable";

export default function NotificationRecordingsView() {

    return (
        <>
            <Head>
                {pageTitle("Notification recordings")}
            </Head>
            <MainPage
                title="Notification recordings"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="home" href={homeUri()}/>
                ]}
            >
                <NotificationRecordingsTable/>
            </MainPage>
        </>
    )
}