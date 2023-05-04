import MainLayout from "@components/layouts/MainLayout";
import {useEffect, useState} from "react";
import restCall from "@client/restCall";
import {Alert, Space} from "antd";
import Head from "next/head";
import {projectTitle, title} from "@components/common/Titles";
import MainPage from "@components/layouts/MainPage";
import {homeBreadcrumbs, projectBreadcrumbs} from "@components/common/Breadcrumbs";
import {CloseToHomeCommand} from "@components/common/Commands";

export default function ExtensionLicenseInfoPage() {

    const [license, setLicense] = useState({})

    useEffect(() => {
        restCall('/extension/license').then(data => {
            setLicense(data.license)
        })
    }, [])

    return (
        <>
            <main>
                <Head>
                    {title('License info')}
                </Head>
                <MainLayout>
                    <MainPage
                        title="License information"
                        breadcrumbs={homeBreadcrumbs()}
                        commands={[
                            <CloseToHomeCommand/>,
                        ]}
                    >
                        {
                            !license &&
                            <Alert message="No license is associated with this instance." type="info"/>
                        }
                        {
                            license &&
                            <dl>
                                <dt>License name</dt>
                                <dd>{license.name}</dd>
                                <dt>License assignee</dt>
                                <dd>{license.assignee}</dd>
                                <dt>Valid until</dt>
                                <dd>
                                    <span ng-if="license.validUntil">{license.validUntil}</span>
                                    <span ng-if="!license.validUntil">Unlimited</span>
                                </dd>
                                <dt>Max projects</dt>
                                <dd>
                                    <span ng-if="license.maxProjects > 0">{license.maxProjects}</span>
                                    <span ng-if="license.maxProjects <= 0">Unlimited</span>
                                </dd>
                            </dl>
                        }
                    </MainPage>
                </MainLayout>
            </main>

        </>
    )
}