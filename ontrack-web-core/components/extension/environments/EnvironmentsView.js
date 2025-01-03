import Head from "next/head";
import {pageTitle} from "@components/common/Titles";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseCommand} from "@components/common/Commands";
import {homeUri} from "@components/common/Links";
import MainPage from "@components/layouts/MainPage";
import EnvironmentCreateCommand from "@components/extension/environments/EnvironmentCreateCommand";
import EnvironmentList from "@components/extension/environments/EnvironmentList";
import SlotCreateCommand from "@components/extension/environments/SlotCreateCommand";
import EnvironmentsWarning from "@components/extension/environments/EnvironmentsWarning";

export default function EnvironmentsView() {
    return (
        <>
            <Head>
                {pageTitle("Environments")}
            </Head>
            <MainPage
                title="Environments"
                warning={<EnvironmentsWarning/>}
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <EnvironmentCreateCommand key="create-environment"/>,
                    <SlotCreateCommand key="create-slot"/>,
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <EnvironmentList/>
            </MainPage>
        </>
    )
}