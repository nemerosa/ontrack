import Head from "next/head";
import {title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import PageSection from "@components/common/PageSection";
import JsonSchemasTable from "@components/core/ref/JsonSchemasTable";

export default function ResourcesView() {
    return (
        <>
            <Head>
                {title("Resources")}
            </Head>
            <MainPage
                title="Resources"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <PageSection title="JSON schemas">
                    <JsonSchemasTable/>
                </PageSection>
            </MainPage>
        </>
    )
}