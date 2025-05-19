import Head from "next/head";
import {title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs} from "@components/common/Breadcrumbs";
import AccountManagementLink, {
    accountManagementUri
} from "@components/core/admin/account-management/AccountManagementLink";
import {CloseCommand} from "@components/common/Commands";
import {Space} from "antd";

export default function GlobalPermissionsView() {
    return (
        <>
            <Head>
                {title("Global permissions")}
            </Head>
            <MainPage
                title="Global permissions"
                breadcrumbs={[
                    ...homeBreadcrumbs(),
                    <AccountManagementLink key="account-management"/>,
                ]}
                commands={[
                    <CloseCommand key="close" href={accountManagementUri}/>,
                ]}
            >
                <Space direction="vertical" className="ot-line">
                </Space>
            </MainPage>
        </>
    )
}