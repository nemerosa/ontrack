import Head from "next/head";
import {title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import UserProfileTokens from "@components/core/admin/userProfile/UserProfileTokens";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import PageSection from "@components/common/PageSection";
import UserAccountCommand from "@components/core/admin/userProfile/UserAccountCommand";
import UserProfileGroups from "@components/core/admin/userProfile/UserProfileGroups";

export default function UserProfileView() {
    return (
        <>
            <Head>
                {title("User profile")}
            </Head>
            <MainPage
                title="User profile"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <UserAccountCommand key="account"/>,
                    <CloseCommand key="close" href={homeUri()}/>,
                ]}
            >
                <PageSection title="API tokens" padding={true}>
                    <UserProfileTokens/>
                </PageSection>
                <PageSection title="Groups" padding={true}>
                    <UserProfileGroups/>
                </PageSection>
            </MainPage>
        </>
    )
}