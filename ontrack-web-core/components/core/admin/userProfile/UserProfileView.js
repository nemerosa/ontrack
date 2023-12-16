import {useContext} from "react";
import {UserContext} from "@components/providers/UserProvider";
import Section from "@components/common/Section";
import Head from "next/head";
import {title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import UserProfileChangePassword from "@components/core/admin/userProfile/UserProfileChangePassword";
import UserProfileTokens from "@components/core/admin/userProfile/UserProfileTokens";
import {homeUri} from "@components/common/Links";
import {CloseCommand} from "@components/common/Commands";

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
                    <Section title="Change password" padding={16}>
                        <UserProfileChangePassword/>
                    </Section>
                }
                <Section title="API tokens" padding={16}>
                    <UserProfileTokens/>
                </Section>
            </MainPage>
        </>
    )
}