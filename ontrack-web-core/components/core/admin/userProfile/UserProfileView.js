import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import Head from "next/head";
import {title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import UserProfileChangePassword from "@components/core/admin/userProfile/UserProfileChangePassword";
import UserProfileTokens from "@components/core/admin/userProfile/UserProfileTokens";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";
import PageSection from "@components/common/PageSection";

export default function UserProfileView() {

    const {authorizations} = useContext(UserContext)

    return (
        <>
            <Head>
                {title("User profile")}
            </Head>
            <MainPage
                title="User profile"
                breadcrumbs={homeBreadcrumbs()}
                commands={[
                    <CloseCommand key="close" href={homeUri()}/>
                ]}
            >
                {
                    authorizations?.user?.changePassword &&
                    <PageSection title="Change password" padding={true}>
                        <UserProfileChangePassword/>
                    </PageSection>
                }
                <PageSection title="API tokens" padding={true}>
                    <UserProfileTokens/>
                </PageSection>
            </MainPage>
        </>
    )
}