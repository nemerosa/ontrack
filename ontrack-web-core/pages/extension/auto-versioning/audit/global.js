import StandardPage from "@components/layouts/StandardPage";
import AutoVersioningAuditView from "@components/extension/auto-versioning/AutoVersioningAuditView";
import {projectBreadcrumbs} from "@components/common/Breadcrumbs";
import AutoVersioningAuditContextProvider from "@components/extension/auto-versioning/AutoVersioningAuditContext";

export default function AutoVersioningAuditGlobalPage() {
    return (
        <>
            <StandardPage
                pageTitle="Auto-versioning audit"
                breadcrumbs={projectBreadcrumbs()}
            >
                <AutoVersioningAuditContextProvider>
                    <AutoVersioningAuditView/>
                </AutoVersioningAuditContextProvider>
            </StandardPage>
        </>
    )
}