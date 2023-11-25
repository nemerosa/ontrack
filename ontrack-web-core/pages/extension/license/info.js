import {useEffect, useState} from "react";
import {Alert} from "antd";
import StandardPage from "@components/layouts/StandardPage";
import {useRestClient} from "@components/providers/ConnectionContextProvider";

export default function ExtensionLicenseInfoPage() {

    const client = useRestClient()

    const [license, setLicense] = useState({})

    useEffect(() => {
        if (client) {
            client.get('/extension/license').then(data => {
                setLicense(data.license)
            })
        }
    }, [client])

    return (
        <StandardPage
            pageTitle="License info">
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
        </StandardPage>
    )
}